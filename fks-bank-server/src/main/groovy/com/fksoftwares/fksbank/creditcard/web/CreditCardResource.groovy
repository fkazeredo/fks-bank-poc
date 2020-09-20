package com.fksoftwares.fksbank.creditcard.web

import com.fksoftwares.fksbank.core.EntityNotFoundException
import com.fksoftwares.fksbank.core.security.SecurityService
import com.fksoftwares.fksbank.creditcard.CreditCard
import com.fksoftwares.fksbank.creditcard.CreditCardRepository
import com.fksoftwares.fksbank.creditcard.web.input.AdjustCurrentLimitInput
import com.fksoftwares.fksbank.creditcard.web.input.ChangeDueDayInput
import com.fksoftwares.fksbank.creditcard.web.input.ChangeLockedInput
import groovy.transform.PackageScope
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*

import javax.validation.Valid

@RestController
@RequestMapping("/credit-cards")
@PackageScope
class CreditCardResource {

    private Logger logger = LoggerFactory.getLogger(CreditCardResource)

    private CreditCardRepository creditCardRepository
    private SecurityService securityService

    CreditCardResource(CreditCardRepository creditCardRepository, SecurityService securityService) {
        this.creditCardRepository = creditCardRepository
        this.securityService = securityService
    }

    @Transactional
    @PatchMapping("/{id}/current-limit")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("@securityService.hasAuthority('CUSTOMER') and @securityService.hasWriteScope()")
    void adjustCurrentLimit(@PathVariable Long id, @Valid @RequestBody AdjustCurrentLimitInput input){

        def creditCard = findCreditCardById(id)

        validateAuthenticatedUserEquals(creditCard.userProfileId)

        creditCard.adjustLimit(input.currentLimit)

        creditCardRepository.save(creditCard)

        logger.info("Limite atual do cartão {} alterado", id)

    }

    @Transactional
    @PatchMapping("/{id}/due-day")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("@securityService.hasAuthority('CUSTOMER') and @securityService.hasWriteScope()")
    void changeDueDay(@PathVariable Long id, @Valid @RequestBody ChangeDueDayInput input){

        def creditCard = findCreditCardById(id)

        validateAuthenticatedUserEquals(creditCard.userProfileId)

        creditCard.changeDueDay(input.dueDay)

        creditCardRepository.save(creditCard)

        logger.info("Dia de vencimento do cartão {} alterado", id)

    }

    @Transactional
    @PatchMapping("/{id}/locked")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("@securityService.hasAuthority('CUSTOMER') and @securityService.hasWriteScope()")
    void changeLocked(@PathVariable Long id, @Valid @RequestBody ChangeLockedInput input){

        def creditCard = findCreditCardById(id)

        validateAuthenticatedUserEquals(creditCard.userProfileId)

        if (input.locked)
            creditCard.lock()
        else
            creditCard.unlock()

        creditCardRepository.save(creditCard)

        logger.info("Cartão {} teve seu bloqueio mudado para: ", creditCard.locked)

    }

    private CreditCard findCreditCardById(Long id){
        return creditCardRepository.findById(id).orElseThrow(
                { -> new EntityNotFoundException("creditCardNotFound", id.toString()) }
        )
    }

    private void validateAuthenticatedUserEquals(Long id){
        if (!this.securityService.authenticatedUserEquals(id))
            throw new AccessDeniedException(id.toString())
    }

}
