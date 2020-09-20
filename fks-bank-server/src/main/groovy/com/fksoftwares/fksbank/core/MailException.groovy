package com.fksoftwares.fksbank.core

class MailException extends BusinessException {

    String mail

    MailException(String message, String mail) {
        super(message)
        this.mail = mail
    }
}
