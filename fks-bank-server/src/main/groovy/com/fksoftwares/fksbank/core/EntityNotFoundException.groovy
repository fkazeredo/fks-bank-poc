package com.fksoftwares.fksbank.core

class EntityNotFoundException extends BusinessException {

    String identifier

    EntityNotFoundException(String message, String identifier) {
        super(message)
        this.identifier = identifier
    }
}
