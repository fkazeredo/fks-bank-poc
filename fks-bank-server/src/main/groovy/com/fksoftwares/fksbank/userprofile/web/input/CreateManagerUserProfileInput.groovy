package com.fksoftwares.fksbank.userprofile.web.input


import org.hibernate.validator.constraints.br.CPF

import javax.validation.Valid
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

class CreateManagerUserProfileInput {

    @CPF
    @NotBlank
    String cpf

    @NotBlank
    @Size(min = 1, max = 150)
    String firstName

    @NotBlank
    @Size(min = 1, max = 150)
    String lastName

    @Email
    @NotBlank
    @Size(min = 1, max = 150)
    String username

    @Size(min = 1, max = 50)
    String phone

    @Valid
    AddressInput address

}
