package com.fksoftwares.fksbank.creditcard.job

import com.fksoftwares.fksbank.creditcard.InvoiceClosing
import groovy.transform.PackageScope
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

import java.time.LocalDate

@Component
@PackageScope
class InvoiceClosingScheduler {

    private InvoiceClosing invoiceClosing

    InvoiceClosingScheduler(InvoiceClosing invoiceClosing) {
        this.invoiceClosing = invoiceClosing
    }

    @Scheduled(cron = "0 0 0 * * ?")
    //@Scheduled(cron = "0 */1 * * * ?")
    void resolveClosingDate() {

        def yesterday = LocalDate.now().minusDays(1)
        invoiceClosing.applyInterests(yesterday)

    }

}
