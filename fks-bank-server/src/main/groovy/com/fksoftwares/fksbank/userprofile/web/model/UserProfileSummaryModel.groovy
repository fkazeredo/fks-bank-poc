package com.fksoftwares.fksbank.userprofile.web.model

import com.fksoftwares.fksbank.userprofile.UserProfileStatus

class UserProfileSummaryModel {

    Long id
    String cpf
    String firstName
    String lastName
    String username
    Boolean enabled
    UserProfileStatus status

    UserProfileSummaryModel(Long id, String cpf, String firstName, String lastName, String username, Boolean enabled, UserProfileStatus status) {
        this.id = id
        this.cpf = cpf
        this.firstName = firstName
        this.lastName = lastName
        this.username = username
        this.enabled = enabled
        this.status = status
    }

    UserProfileSummaryModel(){}

}
