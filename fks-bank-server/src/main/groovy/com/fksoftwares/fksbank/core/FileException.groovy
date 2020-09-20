package com.fksoftwares.fksbank.core

class FileException extends BusinessException {

    String nameOrPath

    FileException(String message, String nameOrPath) {
        super(message)
        this.nameOrPath = nameOrPath
    }
}
