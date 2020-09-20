package com.fksoftwares.fksbank.userprofile.border

import com.fksoftwares.fksbank.userprofile.web.input.AddressInput
import com.fksoftwares.fksbank.userprofile.web.input.ChangeUserProfilePersonalInfoInput
import com.github.javafaker.Faker
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import javax.validation.Validation
import javax.validation.Validator
import javax.validation.ValidatorFactory

class ChangeUserProfilePersonalInfoInputTest {

    private ValidatorFactory factory = Validation.buildDefaultValidatorFactory()
    private Validator validator = factory.getValidator()

    private Faker faker = new Faker()
    private String firstName
    private String lastName
    private String mail
    private String phone
    private AddressInput addressInput

    @BeforeEach
    void setUp() {
        firstName = faker.name().firstName()
        lastName = faker.name().lastName()
        mail = faker.internet().emailAddress()
        phone = faker.number().digits(10)

        addressInput = new AddressInput()
        addressInput.setZipCode(faker.number().digits(8))
        addressInput.setStreet(faker.address().streetName())
        addressInput.setNumber(faker.number().digits(2))
        addressInput.setComplement(faker.lorem().fixedString(50))
        addressInput.setNeighborhood(faker.lorem().fixedString(50))
        addressInput.setCity(faker.address().cityName())
    }

    @Test
    void "WHEN validate change user profile personal info input with all attributes valid, THEN should not have violations"() {

        def input = new ChangeUserProfilePersonalInfoInput(
                firstName: firstName,
                lastName: lastName,
                mail: mail,
                phone: phone,
                address: addressInput
        )

        def violations = validator.validate(input)

       assert violations.isEmpty()

    }

    @Test
    void "WHEN validate change user profile personal info input with null first name, THEN should have violations"() {

        def input = new ChangeUserProfilePersonalInfoInput(
                firstName: null,
                lastName: lastName,
                mail: mail,
                phone: phone,
                address: addressInput
        )

        def violations = validator.validate(input)

        assert !violations.isEmpty()

    }

    @Test
    void "WHEN validate change user profile personal info input with empty first name, THEN should have violations"() {

        def input = new ChangeUserProfilePersonalInfoInput(
                firstName: "",
                lastName: lastName,
                mail: mail,
                phone: phone,
                address: addressInput
        )

        def violations = validator.validate(input)

        assert !violations.isEmpty()

    }

    @Test
    void "WHEN validate change user profile personal info input with first name greater than 150 characters, THEN should have violations"() {

        def input = new ChangeUserProfilePersonalInfoInput(
                firstName: faker.lorem().characters(151),
                lastName: lastName,
                mail: mail,
                phone: phone,
                address: addressInput
        )

        def violations = validator.validate(input)

        assert !violations.isEmpty()

    }

    @Test
    void "WHEN validate change user profile personal info input with null last name, THEN should have violations"() {

        def input = new ChangeUserProfilePersonalInfoInput(
                firstName: firstName,
                lastName: null,
                mail: mail,
                phone: phone,
                address: addressInput
        )

        def violations = validator.validate(input)

        assert !violations.isEmpty()

    }

    @Test
    void "WHEN validate change user profile personal info input with empty last name, THEN should have violations"() {

        def input = new ChangeUserProfilePersonalInfoInput(
                firstName: firstName,
                lastName: "",
                mail: mail,
                phone: phone,
                address: addressInput
        )

        def violations = validator.validate(input)

        assert !violations.isEmpty()

    }

    @Test
    void "WHEN validate change user profile personal info input with last name greater than 150 characters, THEN should have violations"() {


        def input = new ChangeUserProfilePersonalInfoInput(
                firstName: firstName,
                lastName: faker.lorem().characters(151),
                mail: mail,
                phone: phone,
                address: addressInput
        )

        def violations = validator.validate(input)

        assert !violations.isEmpty()

    }

    @Test
    void "WHEN validate change user profile personal info input with an invalid mail, THEN should have violations"() {

        def input = new ChangeUserProfilePersonalInfoInput(
                firstName: firstName,
                lastName: lastName,
                mail: "admin.mail",
                phone: phone,
                address: addressInput
        )

        def violations = validator.validate(input)

        assert !violations.isEmpty()

    }

    @Test
    void "WHEN validate change user profile personal info input with null mail, THEN should have violations"() {

        def input = new ChangeUserProfilePersonalInfoInput(
                firstName: firstName,
                lastName: lastName,
                mail: null,
                phone: phone,
                address: addressInput
        )

        def violations = validator.validate(input)

        assert !violations.isEmpty()

    }

    @Test
    void "WHEN validate change user profile personal info input with empty mail, THEN should have violations"() {

        def input = new ChangeUserProfilePersonalInfoInput(
                firstName: firstName,
                lastName: lastName,
                mail: "",
                phone: phone,
                address: addressInput
        )

        def violations = validator.validate(input)

        assert !violations.isEmpty()

    }

    @Test
    void "WHEN validate change user profile personal info input with mail greater than 150 characters, THEN should have violations"() {

        def input = new ChangeUserProfilePersonalInfoInput(
                firstName: firstName,
                lastName: lastName,
                mail: faker.lorem().characters(151),
                phone: phone,
                address: addressInput
        )

        def violations = validator.validate(input)

        assert !violations.isEmpty()

    }

    @Test
    void "WHEN validate change user profile personal info input with null phone, THEN should not have violations"() {

        def input = new ChangeUserProfilePersonalInfoInput(
                firstName: firstName,
                lastName: lastName,
                mail: mail,
                phone: null,
                address: addressInput
        )

        def violations = validator.validate(input)

       assert violations.isEmpty()

    }

    @Test
    void "WHEN validate change user profile personal info input with empty phone, THEN should have violations"() {

        def input = new ChangeUserProfilePersonalInfoInput(
                firstName: firstName,
                lastName: lastName,
                mail: mail,
                phone: "",
                address: addressInput
        )

        def violations = validator.validate(input)

        assert !violations.isEmpty()

    }

    @Test
    void "WHEN validate change user profile personal info input with phone greater than 50 characters, THEN should have violations"() {

        def input = new ChangeUserProfilePersonalInfoInput(
                firstName: firstName,
                lastName: lastName,
                mail: mail,
                phone: faker.lorem().characters(51),
                address: addressInput
        )

        def violations = validator.validate(input)

        assert !violations.isEmpty()

    }

    @Test
    void "WHEN validate change user profile personal info input with null address, THEN should have violations"() {

        def input = new ChangeUserProfilePersonalInfoInput(
                firstName: firstName,
                lastName: lastName,
                mail: mail,
                phone: phone,
                address: null
        )

        def violations = validator.validate(input)

       assert violations.isEmpty()

    }

    @Test
    void "WHEN validate change user profile personal info input with an invalid address, THEN should have violations"() {

        def input = new ChangeUserProfilePersonalInfoInput(
                firstName: firstName,
                lastName: lastName,
                mail: mail,
                phone: phone,
                address: new AddressInput()
        )

        def violations = validator.validate(input)

        assert !violations.isEmpty()

    }

}
