package com.fksoftwares.fksbank.userprofile


import javax.persistence.Embeddable
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

@Embeddable
class Name {

    @NotBlank
    @Size(min = 1, max = 150)
    private String firstName

    @NotBlank
    @Size(min = 1, max = 150)
    private String lastName

    Name(String firstName, String lastName) {
        this.firstName = firstName
        this.lastName = lastName
    }

    String getFullName(){
        "${this.firstName} ${this.lastName}"
    }

    String getFirstName() {
        return firstName
    }

    String getLastName() {
        return lastName
    }

    // JPA requirement
    protected Name(){}

}
