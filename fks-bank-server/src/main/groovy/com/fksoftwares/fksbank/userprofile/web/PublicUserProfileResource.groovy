package com.fksoftwares.fksbank.userprofile.web

import com.fksoftwares.fksbank.core.BusinessException
import com.fksoftwares.fksbank.core.EntityAlreadyExistsException
import com.fksoftwares.fksbank.core.EntityNotFoundException
import com.fksoftwares.fksbank.userprofile.*
import com.fksoftwares.fksbank.userprofile.web.assembler.UserProfileModelAssembler
import com.fksoftwares.fksbank.userprofile.web.input.ChangeUserProfilePasswordInput
import com.fksoftwares.fksbank.userprofile.web.input.CreateCustomerUserProfileInput
import com.fksoftwares.fksbank.userprofile.web.input.RequestPasswordChangeInput
import com.fksoftwares.fksbank.userprofile.web.model.UserProfileSummaryModel
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*

import javax.validation.Valid

@RestController
@RequestMapping("/public/user-profiles")
class PublicUserProfileResource {

    private Logger logger = LoggerFactory.getLogger(PublicUserProfileResource)

    private UserProfileRepository userProfileRepository
    private UserProfileMailer userProfileMailer

    PublicUserProfileResource(UserProfileRepository userProfileRepository, UserProfileMailer userProfileMailer) {
        this.userProfileRepository = userProfileRepository
        this.userProfileMailer = userProfileMailer
    }

    @GetMapping("/{passwordRecoveryToken}")
    UserProfileSummaryModel findByPasswordRecoveryToken(@PathVariable String passwordRecoveryToken) {

        def userProfile = findUserProfileByPasswordRecoveryToken(passwordRecoveryToken)

        if (userProfile.status == UserProfileStatus.PENDING || userProfile.status == UserProfileStatus.REJECTED)
            throw new BusinessException("pendingOrRejectedUser")

        userProfile.validatePasswordRecoveryToken()

        return UserProfileModelAssembler.toSummary(userProfile)
    }

    @Transactional
    @PostMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void signUp(@Valid @RequestBody CreateCustomerUserProfileInput input) {

        def userProfile = new UserProfile(
                input.getCpf(),
                new Name(input.getFirstName(), input.getLastName()),
                new Contact(input.getUsername(), input.getPhone())
        )

        if (userProfileRepository.exists(userProfile))
            throw new EntityAlreadyExistsException("userProfileAlreadyExists", userProfile.getUsername())

        userProfileRepository.save(userProfile)

        userProfileMailer.sendGreetings(userProfile)

        logger.info("Novo usuário {} se registrou no sistema", userProfile.getUsername())
    }

    @Transactional
    @PostMapping("/password-recovery-token")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void requestPasswordChange(@Valid @RequestBody RequestPasswordChangeInput input) {

        def userProfile = findUserProfileByMail(input.getMail())

        if (userProfile.status == UserProfileStatus.PENDING || userProfile.status == UserProfileStatus.REJECTED)
            throw new BusinessException("pendingOrRejectedUser")

        userProfile.generatePasswordRecoveryToken()

        userProfileRepository.save(userProfile)

        userProfileMailer.sendPasswordRecoveryLink(userProfile)

        logger.info("Usuário {} requisitou mudança de senha", userProfile.getUsername())

    }

    @Transactional
    @PatchMapping("/{passwordRecoveryToken}/password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void changePasswordByPasswordRecoveryToken(@PathVariable String passwordRecoveryToken, @Valid @RequestBody ChangeUserProfilePasswordInput input) {

        def userProfile = findUserProfileByPasswordRecoveryToken(passwordRecoveryToken)

        if (userProfile.status == UserProfileStatus.PENDING || userProfile.status == UserProfileStatus.REJECTED)
            throw new BusinessException("pendingOrRejectedUser")

        userProfile.changePasswordByRecoveryToken(new Password(
                input.getPassword(),
                input.getPasswordConfirmation(),
                [userProfile.getName().getFirstName(), userProfile.getName().getLastName()] as String[]))

        userProfileRepository.save(userProfile)

        logger.info("Senha do usuário {} redefinida", userProfile.getUsername())

    }

    private UserProfile findUserProfileByPasswordRecoveryToken(String passwordRecoveryToken) {
        return userProfileRepository.findByPasswordRecoveryTokenValue(passwordRecoveryToken).orElseThrow(
                { -> new EntityNotFoundException("userProfileNotFound", passwordRecoveryToken) }
        )
    }

    private UserProfile findUserProfileByMail(String mail) {
        return userProfileRepository.findByUsername(mail).orElseThrow(
                { -> new EntityNotFoundException("userProfileNotFound", mail) }
        )
    }

}
