package com.fksoftwares.fksbank.userprofile.web.input


import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

class ChangeUserProfilePasswordInput {

    @NotBlank
    @Size(min = 1, max = 255)
    String password

    @NotBlank
    @Size(min = 1, max = 255)
    String passwordConfirmation

}
