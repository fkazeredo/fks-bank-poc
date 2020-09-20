package com.fksoftwares.fksbank.userprofile.web.filter

import com.fksoftwares.fksbank.userprofile.Permission
import com.fksoftwares.fksbank.userprofile.UserProfileStatus

class UserProfileSearchFilter {
    String name
    String mail
    Boolean isEnabled
    Permission permission
    UserProfileStatus status
}
