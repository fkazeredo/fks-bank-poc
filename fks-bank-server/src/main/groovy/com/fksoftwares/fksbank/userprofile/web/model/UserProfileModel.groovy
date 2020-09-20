package com.fksoftwares.fksbank.userprofile.web.model

import com.fksoftwares.fksbank.userprofile.Permission
import com.fksoftwares.fksbank.userprofile.UserProfileStatus
import com.fksoftwares.fksbank.userprofile.web.input.AddressInput

class UserProfileModel {

    Long id
    String cpf
    String firstName
    String lastName
    String username
    String phone
    AddressInput address
    Set<Permission> permissions
    Boolean enabled
    UserProfileStatus status

}
