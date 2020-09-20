package com.fksoftwares.fksbank.userprofile.web.input


import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

class AddressInput {

    @NotBlank
    @Size(min = 1, max = 50)
    String zipCode

    @NotBlank
    @Size(min = 1, max = 255)
    String street

    @NotBlank
    @Size(min = 1, max = 50)
    String number

    @Size(min = 1, max = 150)
    String complement

    @NotBlank
    @Size(min = 1, max = 150)
    String neighborhood

    @NotBlank
    @Size(min = 1, max = 150)
    String city

}
