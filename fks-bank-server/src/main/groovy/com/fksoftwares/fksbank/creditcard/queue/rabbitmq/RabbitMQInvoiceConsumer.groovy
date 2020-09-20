package com.fksoftwares.fksbank.creditcard.queue.rabbitmq


import com.fksoftwares.fksbank.creditcard.Purchase
import com.fksoftwares.fksbank.creditcard.PurchaseCreated
import groovy.transform.PackageScope
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.context.annotation.Profile
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Profile("dev")
@Component
@PackageScope
class RabbitMQInvoiceConsumer {

    private Purchase purchase

    RabbitMQInvoiceConsumer(Purchase purchase) {
        this.purchase = purchase
    }

    @Transactional
    @RabbitListener(queues = ["\${spring.rabbitmq.queue.creditcard-purchase.name}"])
    void receive(@Payload PurchaseCreated event) {
        event.handle(purchase)
    }

}
