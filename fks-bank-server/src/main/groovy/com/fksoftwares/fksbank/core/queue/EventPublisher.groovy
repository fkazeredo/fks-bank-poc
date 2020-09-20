package com.fksoftwares.fksbank.core.queue

import com.fksoftwares.fksbank.core.DomainEvent

interface EventPublisher {

    void send(String route, DomainEvent event)

}