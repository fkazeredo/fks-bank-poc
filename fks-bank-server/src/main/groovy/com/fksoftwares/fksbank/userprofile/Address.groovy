package com.fksoftwares.fksbank.userprofile


import javax.persistence.Embeddable
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

@Embeddable
class Address {

    @NotBlank
    @Size(min = 1, max = 50)
    private String zipCode

    @NotBlank
    @Size(min = 1, max = 255)
    private String street

    @NotBlank
    @Size(min = 1, max = 50)
    private String number

    @Size(min = 1, max = 150)
    private String complement

    @NotBlank
    @Size(min = 1, max = 150)
    private String neighborhood

    @NotBlank
    @Size(min = 1, max = 150)
    private String city

    Address(String zipCode, String street, String number, String complement, String neighborhood, String city) {
        this.zipCode = zipCode
        this.street = street
        this.number = number
        this.complement = complement
        this.neighborhood = neighborhood
        this.city = city
    }

    String getFullAddress(){
        def complementText = ""
        if (this.complement != null)
            this.complement = "/ " + this.complement
        this.street + " nº " + this.number + " " + complementText + ", " + this.neighborhood + " - " + this.city + " - " + this.zipCode
    }

    String getZipCode() {
        return zipCode
    }

    String getStreet() {
        return street
    }

    String getNumber() {
        return number
    }

    String getComplement() {
        return complement
    }

    String getNeighborhood() {
        return neighborhood
    }

    String getCity() {
        return city
    }

    // JPA requirement
    protected Address(){}

}
