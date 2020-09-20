package com.fksoftwares.fksbank.creditcard.queue.rabbitmq


import com.fksoftwares.fksbank.creditcard.CreditCardRepository
import com.fksoftwares.fksbank.creditcard.UserProfileCreated
import groovy.transform.PackageScope
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.context.annotation.Profile
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Profile("dev")
@Component
@PackageScope
class RabbitMQCreditCardConsumer {

    private CreditCardRepository creditCardRepository

    RabbitMQCreditCardConsumer(CreditCardRepository creditCardRepository) {
        this.creditCardRepository = creditCardRepository
    }

    @Transactional
    @RabbitListener(queues = ["\${spring.rabbitmq.queue.userprofile-creation.name}"])
    void receive(@Payload UserProfileCreated event) {

        event.handle(creditCardRepository)

    }

}
