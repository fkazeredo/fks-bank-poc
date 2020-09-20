package com.fksoftwares.fksbank.creditcard.queue.local

import com.fksoftwares.fksbank.creditcard.CreditCardRepository
import com.fksoftwares.fksbank.creditcard.UserProfileCreated
import groovy.transform.PackageScope
import org.springframework.context.annotation.Profile
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Profile("test")
@Component
@PackageScope
class LocalCreditCardConsumer {

    private CreditCardRepository creditCardRepository

    LocalCreditCardConsumer(CreditCardRepository creditCardRepository) {
        this.creditCardRepository = creditCardRepository
    }

    @EventListener
    public void receive(UserProfileCreated event) {
        event.handle(creditCardRepository)
    }

}
