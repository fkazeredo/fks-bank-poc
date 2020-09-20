package com.fksoftwares.fksbank.creditcard.web

import com.fksoftwares.fksbank.core.EntityNotFoundException
import com.fksoftwares.fksbank.creditcard.CreditCard
import com.fksoftwares.fksbank.creditcard.CreditCardRepository
import com.fksoftwares.fksbank.creditcard.InvoiceRepository
import com.fksoftwares.fksbank.creditcard.web.model.CreditCardSummaryModel
import groovy.transform.PackageScope
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/user-profiles/{userProfileId}/credit-cards")
@PackageScope
class UserProfileCreditCardResource {

    private creditCardRepository
    private InvoiceRepository invoiceRepository

    UserProfileCreditCardResource(CreditCardRepository creditCardRepository, InvoiceRepository invoiceRepository) {
        this.creditCardRepository = creditCardRepository
        this.invoiceRepository = invoiceRepository
    }

    @GetMapping
    @PreAuthorize("@securityService.hasAuthority('CUSTOMER') and @securityService.authenticatedUserEquals(#userProfileId) and @securityService.hasWriteScope()")
    CreditCardSummaryModel findCreditSummaryByUserId(@PathVariable Long userProfileId){

        CreditCard creditCard = creditCardRepository.findByUserProfileId(userProfileId).orElseThrow(
                { -> new EntityNotFoundException("creditCardNotFound", userProfileId.toString()) }
        )

        def creditCardSummary = new CreditCardSummaryModel(
                creditCardLastNumbers: creditCard.lastNumbers,
                currentInvoiceTotal: invoiceRepository.findCurrentTotalByCreditCardId(creditCard.id),
                availableLimit: creditCardRepository.findAvailableLimitByUserProfileId(userProfileId)
        )
        return creditCardSummary
    }

}
