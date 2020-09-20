package com.fksoftwares.fksbank.creditcard.web

import com.fksoftwares.fksbank.core.EntityNotFoundException
import com.fksoftwares.fksbank.core.security.SecurityService
import com.fksoftwares.fksbank.creditcard.CreditCard
import com.fksoftwares.fksbank.creditcard.CreditCardRepository
import com.fksoftwares.fksbank.creditcard.Invoice
import com.fksoftwares.fksbank.creditcard.InvoiceRepository
import com.fksoftwares.fksbank.creditcard.web.assembler.InvoiceModelAssembler
import com.fksoftwares.fksbank.creditcard.web.model.InvoiceModel
import com.fksoftwares.fksbank.creditcard.web.model.InvoiceSummaryModel
import com.fksoftwares.fksbank.creditcard.web.input.PaymentInput
import groovy.transform.PackageScope
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.cache.annotation.Cacheable
import org.springframework.http.HttpStatus
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*

import javax.validation.Valid

@RestController
@RequestMapping("/credit-cards/{creditCardId}/invoices")
@PackageScope
class InvoiceResource {

    private Logger logger = LoggerFactory.getLogger(InvoiceResource)

    private InvoiceRepository invoiceRepository
    private CreditCardRepository creditCardRepository
    private SecurityService securityService

    InvoiceResource(InvoiceRepository invoiceRepository, CreditCardRepository creditCardRepository, SecurityService securityService) {
        this.invoiceRepository = invoiceRepository
        this.creditCardRepository = creditCardRepository
        this.securityService = securityService
    }

    @Cacheable(value = "invoiceSummary")
    @GetMapping
    @PreAuthorize("@securityService.hasAuthority('CUSTOMER') and @securityService.hasWriteScope()")
    List<InvoiceSummaryModel> findAllByCreditCardId(@PathVariable Long creditCardId) {

        def creditCard = findCreditCardById(creditCardId)
        validateAuthenticatedUserEquals(creditCard.userProfileId)

        return invoiceRepository.findAllByCreditCardId(creditCardId)
    }

    @GetMapping("/{invoiceId}")
    @PreAuthorize("@securityService.hasAuthority('CUSTOMER') and @securityService.hasWriteScope()")
    InvoiceModel findByCreditCardIdAndInvoiceId(@PathVariable Long creditCardId, @PathVariable Long invoiceId) {

        def creditCard = findCreditCardById(creditCardId)
        validateAuthenticatedUserEquals(creditCard.userProfileId)

        Invoice invoice = findInvoiceByCreditCardIdAndInvoiceId(creditCardId, invoiceId)

        return InvoiceModelAssembler.toModel(invoice)
    }

    @Transactional
    @PostMapping("/{invoiceId}/payment")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("@securityService.hasAuthority('CUSTOMER') and @securityService.hasWriteScope()")
    InvoiceModel pay(@PathVariable Long creditCardId, @PathVariable Long invoiceId, @Valid @RequestBody PaymentInput input) {

        def creditCard = findCreditCardById(creditCardId)
        validateAuthenticatedUserEquals(creditCard.userProfileId)

        Invoice invoice = findInvoiceByCreditCardIdAndInvoiceId(creditCardId, invoiceId)

        invoice.addPayment(input.value)

        invoiceRepository.save(invoice)

        logger.info("Fatura {} paga no valor de {}", invoiceId, input.value)

        return InvoiceModelAssembler.toModel(invoice)

    }

    private Invoice findInvoiceByCreditCardIdAndInvoiceId(Long creditCardId, Long invoiceId) {
        return invoiceRepository.findByCreditCardIdAndInvoiceId(creditCardId, invoiceId).orElseThrow({
            throw new EntityNotFoundException("invoiceNotFound", invoiceId.toString())
        })
    }

    private CreditCard findCreditCardById(Long id) {
        return creditCardRepository.findById(id).orElseThrow(
                { -> new EntityNotFoundException("creditCardNotFound", id.toString()) }
        )
    }

    private void validateAuthenticatedUserEquals(Long id) {
        if (!this.securityService.authenticatedUserEquals(id))
            throw new AccessDeniedException(id.toString())
    }

}
