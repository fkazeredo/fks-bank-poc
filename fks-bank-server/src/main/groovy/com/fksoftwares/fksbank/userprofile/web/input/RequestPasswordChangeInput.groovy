package com.fksoftwares.fksbank.userprofile.web.input


import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank

class RequestPasswordChangeInput {

    @Email
    @NotBlank
    String mail

}
