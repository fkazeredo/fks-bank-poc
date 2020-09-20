package com.fksoftwares.fksbank.userprofile

class PasswordRecoveryTokenExpiredException extends RuntimeException{

    PasswordRecoveryTokenExpiredException(String message) {
        super(message)
    }

}
