package com.fksoftwares.fksbank.userprofile


import javax.persistence.Embeddable
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

@Embeddable
class Contact {

    @Email
    @NotBlank
    @Size(min = 1, max = 150)
    private String mail

    @Size(min = 1, max = 50)
    private String phone

    Contact(String mail, String phone) {
        this.mail = mail
        this.phone = phone
    }

    String getMail() {
        return mail
    }

    String getPhone() {
        return phone
    }

    // JPA requirement
    protected Contact(){}

}
