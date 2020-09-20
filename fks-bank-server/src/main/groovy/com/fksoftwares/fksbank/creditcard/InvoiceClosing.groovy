package com.fksoftwares.fksbank.creditcard

import org.springframework.stereotype.Service
import org.springframework.validation.annotation.Validated

import javax.validation.constraints.NotNull
import java.time.LocalDate
import java.time.LocalDateTime

@Service
@Validated
class InvoiceClosing {

    private InvoiceRepository invoiceRepository

    InvoiceClosing(InvoiceRepository invoiceRepository) {
        this.invoiceRepository = invoiceRepository
    }

    void applyInterests(@NotNull LocalDate date) {

        List<Invoice> invoicesClosedYesterday = invoiceRepository.findAllWithClosingDateOn(date)

        invoicesClosedYesterday.forEach {

            if (it.total < 0) {

                def maybeInvoice = invoiceRepository.findCurrentByCreditCardId(it.creditCard.id)

                Invoice currentInvoice
                if (maybeInvoice.isEmpty())
                    currentInvoice = Invoice.createByLastInvoice(it)
                else
                    currentInvoice = maybeInvoice.get()

                def interest = it.total + (it.total * 0.07)

                currentInvoice.addPurchase(new InvoiceTransaction(
                        Category.INTEREST,
                        LocalDateTime.now(),
                        Category.INTEREST.name(),
                        interest
                ))

                invoiceRepository.save(currentInvoice)

            }

        }

    }

}
