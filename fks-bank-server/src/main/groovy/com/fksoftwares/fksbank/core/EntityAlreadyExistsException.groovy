package com.fksoftwares.fksbank.core

class EntityAlreadyExistsException extends BusinessException {

    String identifier

    EntityAlreadyExistsException(String message, String identifier) {
        super(message)
        this.identifier = identifier
    }
}
