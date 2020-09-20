package com.fksoftwares.fksbank.core

class EntityIsDisabledException extends BusinessException{

    String identifier

    EntityIsDisabledException(String message, String identifier) {
        super(message)
        this.identifier = identifier
    }
}
