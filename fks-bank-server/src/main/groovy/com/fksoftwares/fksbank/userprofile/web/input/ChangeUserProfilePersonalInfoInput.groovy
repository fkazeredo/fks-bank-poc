package com.fksoftwares.fksbank.userprofile.web.input


import javax.validation.Valid
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

class ChangeUserProfilePersonalInfoInput {

    @NotBlank
    @Size(min = 1, max = 150)
    String firstName

    @NotBlank
    @Size(min = 1, max = 150)
    String lastName

    @Email
    @NotBlank
    @Size(min = 1, max = 150)
    String mail

    @Size(min = 1, max = 50)
    String phone

    @Valid
    AddressInput address

}
