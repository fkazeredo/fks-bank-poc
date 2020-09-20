package com.fksoftwares.fksbank.creditcard.queue.local

import com.fksoftwares.fksbank.creditcard.Purchase
import com.fksoftwares.fksbank.creditcard.PurchaseCreated
import groovy.transform.PackageScope
import org.springframework.context.annotation.Profile
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Profile("test")
@Component
@PackageScope
class LocalInvoiceConsumer {

    private Purchase purchase

    LocalInvoiceConsumer(Purchase purchase) {
        this.purchase = purchase
    }

    @EventListener
    public void receive(PurchaseCreated event) {
        event.handle(purchase)
    }

}
