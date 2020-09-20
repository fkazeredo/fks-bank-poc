package com.fksoftwares.fksbank.core.queue.impl

import com.fksoftwares.fksbank.core.DomainEvent
import com.fksoftwares.fksbank.core.queue.EventPublisher
import groovy.transform.PackageScope
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Profile("test")
@Component
@PackageScope
class LocalEventPublisher implements EventPublisher{

    private ApplicationEventPublisher publisher

    LocalEventPublisher(ApplicationEventPublisher publisher) {
        this.publisher = publisher
    }

    @Override
    void send(String route, DomainEvent event) {
        publisher.publishEvent(event)
    }

}
