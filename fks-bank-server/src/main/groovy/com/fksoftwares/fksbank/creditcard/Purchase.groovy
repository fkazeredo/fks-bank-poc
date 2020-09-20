package com.fksoftwares.fksbank.creditcard

import com.fksoftwares.fksbank.core.BusinessException
import com.fksoftwares.fksbank.core.EntityNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.validation.annotation.Validated

import javax.validation.Valid
import javax.validation.constraints.NotNull
import java.math.RoundingMode

@Service
@Validated
class Purchase {

    private InvoiceRepository invoiceRepository
    private CreditCardRepository creditCardRepository

    Purchase(InvoiceRepository invoiceRepository, CreditCardRepository creditCardRepository) {
        this.invoiceRepository = invoiceRepository
        this.creditCardRepository = creditCardRepository
    }

    @Transactional(propagation = Propagation.MANDATORY)
    Invoice execute(@NotNull Long creditCardId, @NotNull @Valid InvoiceTransaction anInvoiceTransaction) {

        def currentInvoice = findCurrentInvoiceByCreditCardId(creditCardId)
        currentInvoice.addPurchase(anInvoiceTransaction)

        return invoiceRepository.save(currentInvoice)

    }

    @Transactional
    void execute(@NotNull Long creditCardId, @NotNull Integer installments,
                 @NotNull Boolean hasInstallmentsInterest, @NotNull @Valid InvoiceTransaction anInvoiceTransaction) {

        def creditCard = findCreditCardById(creditCardId)
        def availableNegativeLimit = -1 * creditCardRepository.findAvailableLimitByUserProfileId(creditCard.userProfileId)

        if (anInvoiceTransaction.value < availableNegativeLimit)
            throw new BusinessException("purchaseCreditCardNoLimit")

        def category = anInvoiceTransaction.getCategory()
        def date = anInvoiceTransaction.getDate()
        def value = anInvoiceTransaction.getValue()
        def description = anInvoiceTransaction.getDescription()

        if (hasInstallmentsInterest && installments > 0) {
            def interestRate = 0.04
            def valueWithInterest = value + (value * interestRate)
            value = valueWithInterest.divide(new BigDecimal(installments), 4, RoundingMode.HALF_EVEN)
        }

        def currentInvoice = execute(creditCardId, new InvoiceTransaction(category, date, description, value))

        def nextInvoices = findNextInvoices(creditCardId, currentInvoice)
        def invoicesToCreate = installments - nextInvoices.size()

        if (invoicesToCreate > 0) {

            def invoice = nextInvoices.size() > 0 ? nextInvoices.get(nextInvoices.size() - 1) : currentInvoice

            for (int i in 1..invoicesToCreate - 1) {
                def nextInvoice = Invoice.createByLastInvoice(invoice)
                date = date.plusMonths(1)
                nextInvoices.add(nextInvoice)
                invoice = nextInvoice
            }

        }

        if (installments > 0)
            nextInvoices.each {
                def nextMonth = date.withMonth(it.startDate.month.value)
                it.addPurchase(new InvoiceTransaction(category, nextMonth, description, value))
                invoiceRepository.save(it)
            }

    }

    private CreditCard findCreditCardById(Long id) {
        return creditCardRepository.findById(id).orElseThrow(
                { -> new EntityNotFoundException("creditCardNotFound", id.toString()) }
        )
    }

    private Invoice findCurrentInvoiceByCreditCardId(Long creditCardId) {
        return invoiceRepository.findCurrentByCreditCardId(creditCardId).orElseThrow(
                { -> new EntityNotFoundException("currentInvoiceNotFound", String.valueOf(creditCardId)) }
        )
    }

    private List<Invoice> findNextInvoices(Long creditCardId, Invoice currentInvoice) {
        return invoiceRepository.findAllByCreditCardIdAfterDate(creditCardId, currentInvoice.closingDate)
    }

}
