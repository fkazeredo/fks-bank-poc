package com.fksoftwares.fksbank.userprofile

enum  Permission {

    MANAGER("Gerente"),
    CUSTOMER("Cliente")

    Permission(String description) {
        this.description = description
    }

    private String description

    String getDescription() {
        return description
    }
}
