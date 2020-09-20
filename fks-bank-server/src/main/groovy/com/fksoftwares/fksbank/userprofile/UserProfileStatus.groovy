package com.fksoftwares.fksbank.userprofile

enum UserProfileStatus {
    PENDING("Pendente"),
    REJECTED("Rejeitado"),
    APPROVED("Aprovado")

    UserProfileStatus(String description) {
        this.description = description
    }

    private String description

    String getDescription() {
        return description
    }
}