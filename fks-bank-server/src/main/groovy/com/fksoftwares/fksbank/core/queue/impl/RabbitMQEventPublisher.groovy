package com.fksoftwares.fksbank.core.queue.impl

import com.fksoftwares.fksbank.core.DomainEvent
import com.fksoftwares.fksbank.core.queue.EventPublisher
import groovy.transform.PackageScope
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Profile("dev")
@Component
@PackageScope
class RabbitMQEventPublisher implements EventPublisher {

    private RabbitTemplate rabbitTemplate

    RabbitMQEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate
    }

    @Override
    void send(String route, DomainEvent event) {
        rabbitTemplate.convertAndSend(route, event)
    }

}
