package com.fksoftwares.fksbank.userprofile.web


import com.fksoftwares.fksbank.core.EntityAlreadyExistsException
import com.fksoftwares.fksbank.core.EntityNotFoundException
import com.fksoftwares.fksbank.core.file.FileUploader
import com.fksoftwares.fksbank.core.queue.EventPublisher
import com.fksoftwares.fksbank.creditcard.UserProfileCreated
import com.fksoftwares.fksbank.userprofile.*
import com.fksoftwares.fksbank.userprofile.web.assembler.UserProfileModelAssembler
import com.fksoftwares.fksbank.userprofile.web.filter.UserProfileSearchFilter
import com.fksoftwares.fksbank.userprofile.web.input.*
import com.fksoftwares.fksbank.userprofile.web.model.UserProfileModel
import com.fksoftwares.fksbank.userprofile.web.model.UserProfileSummaryModel
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

import javax.validation.Valid
import java.security.Principal

@RestController
@RequestMapping("/user-profiles")
class UserProfileResource {

    private Logger logger = LoggerFactory.getLogger(UserProfileResource)

    private UserProfileRepository userProfileRepository
    private EventPublisher eventPublisher
    private UserProfileMailer userProfileMailer
    private FileUploader fileUploader

    @Value("\${spring.rabbitmq.queue.userprofile-creation.name}")
    private String userProfileCreationRoute

    UserProfileResource(UserProfileRepository userProfileRepository, EventPublisher eventPublisher, UserProfileMailer userProfileMailer, FileUploader fileUploader) {
        this.userProfileRepository = userProfileRepository
        this.eventPublisher = eventPublisher
        this.userProfileMailer = userProfileMailer
        this.fileUploader = fileUploader
    }

    @GetMapping
    @PreAuthorize("@securityService.hasAuthority('MANAGER') and @securityService.hasReadScope()")
    Page<UserProfileSummaryModel> searchByFilter(UserProfileSearchFilter filter, Pageable pageable) {
        return userProfileRepository.searchByFilter(filter, pageable)
    }

    @GetMapping("/{id}")
    @PreAuthorize("@securityService.hasAuthority('MANAGER') or @securityService.authenticatedUserEquals(#id) and @securityService.hasReadScope()")
    UserProfileModel findById(@PathVariable Long id) {

        def userProfile = findUserProfileById(id)
        return UserProfileModelAssembler.toModel(userProfile)
    }

    @GetMapping("/{id}/picture")
    @PreAuthorize("@securityService.hasAuthority('MANAGER') or @securityService.authenticatedUserEquals(#id) and @securityService.hasReadScope()")
    byte[] findProfilePictureById(@PathVariable Long id) throws IOException {

        def userProfile = findUserProfileById(id)
        return fileUploader.get(userProfile.getPictureUrl())
    }

    @Transactional
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("@securityService.hasAuthority('MANAGER') and @securityService.hasWriteScope()")
    UserProfileModel createManager(@Valid @RequestBody CreateManagerUserProfileInput input, Principal principal) {

        def userProfile = new UserProfile(
                input.getCpf(),
                new Name(input.getFirstName(), input.getLastName()),
                new Contact(input.getUsername(), input.getPhone()),
                new Address(
                        input.getAddress().getZipCode(),
                        input.getAddress().getStreet(),
                        input.getAddress().getNumber(),
                        input.getAddress().getComplement(),
                        input.getAddress().getNeighborhood(),
                        input.getAddress().getCity()))

        if (userProfileRepository.exists(userProfile))
            throw new EntityAlreadyExistsException("userProfileAlreadyExists", userProfile.getUsername())

        userProfileRepository.save(userProfile)
        userProfileMailer.sendGreetings(userProfile)

        logger.info("Novo usuário com perfil GERENTE {} criado no sistema por {}", userProfile.getUsername(), principal.getName())

        return UserProfileModelAssembler.toModel(userProfile)
    }

    @Transactional
    @PatchMapping("/{id}/personal-info")
    @PreAuthorize("@securityService.authenticatedUserEquals(#id) and @securityService.hasWriteScope()")
    UserProfileModel changePersonalInfo(@PathVariable Long id, @Valid @RequestBody ChangeUserProfilePersonalInfoInput input) {

        def userProfile = findUserProfileById(id)

        def address = input.getAddress() != null ? new Address(
                input.getAddress().getZipCode(),
                input.getAddress().getStreet(),
                input.getAddress().getNumber(),
                input.getAddress().getComplement(),
                input.getAddress().getNeighborhood(),
                input.getAddress().getCity()
        ) : null

        userProfile.changePersonalInfo(
                new Name(input.getFirstName(), input.getLastName()),
                new Contact(input.getMail(), input.getPhone()),
                address)

        if (userProfileRepository.exists(userProfile))
            throw new EntityAlreadyExistsException("userProfileAlreadyExists", String.valueOf(1))

        userProfileRepository.save(userProfile)

        logger.info("Dados pessoais do usuário {} foram modificados por ele mesmo", userProfile.getUsername())

        return UserProfileModelAssembler.toModel(userProfile)

    }

    @Transactional
    @PatchMapping("/{id}/picture")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("@securityService.authenticatedUserEquals(#id) and @securityService.hasWriteScope()")
    void uploadUserProfilePicture(@PathVariable Long id, MultipartFile file) {

        def pictureUrl = fileUploader.upload("/user-profile/" + id, file)
        def userProfile = findUserProfileById(id)

        userProfile.changePictureUrl(pictureUrl)

        userProfileRepository.save(userProfile)

        logger.info("Foto de perfil do usuário {} foi modificada por ele mesmo", userProfile.getUsername())

    }

    @Transactional
    @PatchMapping("/{id}/password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("@securityService.authenticatedUserEquals(#id) and @securityService.hasReadScope()")
    void changeUserProfilePassword(@PathVariable Long id, @Valid @RequestBody ChangeUserProfilePasswordInput input) {

        def userProfile = findUserProfileById(id)

        userProfile.changePassword(
                new Password(
                        input.getPassword(),
                        input.getPasswordConfirmation(),
                        [userProfile.getName().getFirstName(), userProfile.getName().getLastName()] as String[]))

        userProfileRepository.save(userProfile)

        logger.info("Senha do usuário {} foi modificada por ele mesmo", userProfile.getUsername())

    }

    @Transactional
    @PatchMapping("/{id}/approval")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("@securityService.hasAuthority('MANAGER') and @securityService.hasWriteScope()")
    void approve(@PathVariable Long id, @Valid @RequestBody ApproveUserProfileInput input, Principal principal) {

        def userProfile = findUserProfileById(id)
        userProfile.approve()
        userProfileRepository.save(userProfile)

        def userProfileCreated = new UserProfileCreated(
                userProfileId: userProfile.id,
                userProfileName: userProfile.name.fullName,
                maxLimit: input.maxLimit
        )

        eventPublisher.send(userProfileCreationRoute, userProfileCreated)

        userProfileMailer.sendApprovalMessage(userProfile)

        logger.info("Usuário {} foi aprovado por {}", userProfile.getUsername(), principal.getName())

    }

    @Transactional
    @PatchMapping("/{id}/rejection")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("@securityService.hasAuthority('MANAGER') and @securityService.hasWriteScope()")
    void reject(@PathVariable Long id, Principal principal) {

        def userProfile = findUserProfileById(id)
        userProfile.reject()
        userProfileRepository.save(userProfile)

        userProfileMailer.sendRejectionMessage(userProfile)

        logger.info("Usuário {} foi rejeitado por {}", userProfile.getUsername(), principal.getName())

    }

    @Transactional
    @PatchMapping("/{id}/enablement")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("@securityService.hasAuthority('MANAGER') and @securityService.hasWriteScope()")
    UserProfileModel changeEnablement(@PathVariable Long id, @Valid @RequestBody ChangeUserProfileEnablementInput input, Principal principal) {

        def userProfile = findUserProfileById(id)

        if (input.getEnablement())
            userProfile.enable()
        else
            userProfile.disable()

        userProfileRepository.save(userProfile)

        logger.info("Usuário {} teve a visibilidade alterada para {} por {}", userProfile.getUsername(), input.getEnablement(), principal.getName())

        return UserProfileModelAssembler.toModel(userProfile)

    }

    private UserProfile findUserProfileById(Long id) {
        return userProfileRepository.findById(id).orElseThrow(
                { -> new EntityNotFoundException("userProfileNotFound", String.valueOf(id)) }
        )
    }


}
