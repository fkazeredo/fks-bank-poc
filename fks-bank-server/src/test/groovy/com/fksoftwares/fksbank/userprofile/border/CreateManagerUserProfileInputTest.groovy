package com.fksoftwares.fksbank.userprofile.border

import com.fksoftwares.fksbank.userprofile.web.input.AddressInput
import com.fksoftwares.fksbank.userprofile.web.input.CreateManagerUserProfileInput
import com.github.javafaker.Faker
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import javax.validation.Validation
import javax.validation.Validator
import javax.validation.ValidatorFactory

class CreateManagerUserProfileInputTest {

    private ValidatorFactory factory = Validation.buildDefaultValidatorFactory()
    private Validator validator = factory.getValidator()

    private Faker faker = new Faker()
    private String cpf
    private String firstName
    private String lastName
    private String username
    private String phone
    private AddressInput addressInput

    @BeforeEach
    void setUp() {
        cpf = "42452840050"
        firstName = faker.name().firstName()
        lastName = faker.name().lastName()
        username = faker.internet().emailAddress()
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
    void "WHEN validate create manager user profile input with all attributes valid, THEN should not have violations"() {

        def input = new CreateManagerUserProfileInput(
                cpf: cpf,
                firstName: firstName,
                lastName: lastName,
                username: username,
                phone: phone,
                address: addressInput
        )

        def violations = validator.validate(input)

        assert violations.isEmpty()

    }

    @Test
    void "WHEN validate create manager user profile input with an invalid cpf, THEN should have violations"() {

        def input = new CreateManagerUserProfileInput(
                cpf: "12345678921026",
                firstName: firstName,
                lastName: lastName,
                username: username,
                phone: phone,
                address: addressInput
        )

        def violations = validator.validate(input)

        assert !violations.isEmpty()

    }

    @Test
    void "WHEN validate create manager user profile input with null cpf, THEN should have violations"() {

        def input = new CreateManagerUserProfileInput(
                cpf: null,
                firstName: firstName,
                lastName: lastName,
                username: username,
                phone: phone,
                address: addressInput
        )

        def violations = validator.validate(input)

        assert !violations.isEmpty()

    }

    @Test
    void "WHEN validate create manager user profile input with empty cpf, THEN should have violations"() {

        def input = new CreateManagerUserProfileInput(
                cpf: "",
                firstName: firstName,
                lastName: lastName,
                username: username,
                phone: phone,
                address: addressInput
        )

        def violations = validator.validate(input)

        assert !violations.isEmpty()

    }

    @Test
    void "WHEN validate create manager user profile input with null first name, THEN should have violations"() {

        def input = new CreateManagerUserProfileInput(
                cpf: cpf,
                firstName: null,
                lastName: lastName,
                username: username,
                phone: phone,
                address: addressInput
        )

        def violations = validator.validate(input)

        assert !violations.isEmpty()

    }

    @Test
    void "WHEN validate create manager user profile input with empty first name, THEN should have violations"() {

        def input = new CreateManagerUserProfileInput(
                cpf: cpf,
                firstName: "",
                lastName: lastName,
                username: username,
                phone: phone,
                address: addressInput
        )

        def violations = validator.validate(input)

        assert !violations.isEmpty()

    }

    @Test
    void "WHEN validate create manager user profile input with first name greater than 150 characters, THEN should have violations"() {

        def input = new CreateManagerUserProfileInput(
                cpf: cpf,
                firstName: faker.lorem().characters(151),
                lastName: lastName,
                username: username,
                phone: phone,
                address: addressInput
        )

        def violations = validator.validate(input)

        assert !violations.isEmpty()

    }

    @Test
    void "WHEN validate create manager user profile input with null last name, THEN should have violations"() {

        def input = new CreateManagerUserProfileInput(
                cpf: cpf,
                firstName: firstName,
                lastName: null,
                username: username,
                phone: phone,
                address: addressInput
        )

        def violations = validator.validate(input)

        assert !violations.isEmpty()

    }

    @Test
    void "WHEN validate create manager user profile input with empty last name, THEN should have violations"() {

        def input = new CreateManagerUserProfileInput(
                cpf: cpf,
                firstName: firstName,
                lastName: "",
                username: username,
                phone: phone,
                address: addressInput
        )

        def violations = validator.validate(input)

        assert !violations.isEmpty()

    }

    @Test
    void "WHEN validate create manager user profile input with last name greater than 150 characters, THEN should have violations"() {

        def input = new CreateManagerUserProfileInput(
                cpf: cpf,
                firstName: firstName,
                lastName: faker.lorem().characters(151),
                username: username,
                phone: phone,
                address: addressInput
        )

        def violations = validator.validate(input)

        assert !violations.isEmpty()

    }

    @Test
    void "WHEN validate create manager user profile input with an invalid username format, THEN should have violations"() {

        def input = new CreateManagerUserProfileInput(
                cpf: cpf,
                firstName: firstName,
                lastName: lastName,
                username: "customer.com",
                phone: phone,
                address: addressInput
        )

        def violations = validator.validate(input)

        assert !violations.isEmpty()

    }

    @Test
    void "WHEN validate create manager user profile input with null username, THEN should have violations"() {

        def input = new CreateManagerUserProfileInput(
                cpf: cpf,
                firstName: firstName,
                lastName: lastName,
                username: null,
                phone: phone,
                address: addressInput
        )

        def violations = validator.validate(input)

        assert !violations.isEmpty()

    }

    @Test
    void "WHEN validate create manager user profile input with empty username, THEN should have violations"() {

        def input = new CreateManagerUserProfileInput(
                cpf: cpf,
                firstName: firstName,
                lastName: lastName,
                username: "",
                phone: phone,
                address: addressInput
        )

        def violations = validator.validate(input)

        assert !violations.isEmpty()

    }

    @Test
    void "WHEN validate create manager user profile input with username greater than 150 characters, THEN should have violations"() {

        def input = new CreateManagerUserProfileInput(
                cpf: cpf,
                firstName: firstName,
                lastName: lastName,
                username: faker.lorem().characters(151),
                phone: phone,
                address: addressInput
        )

        def violations = validator.validate(input)

        assert !violations.isEmpty()

    }

    @Test
    void "WHEN validate create manager user profile input with null phone, THEN should not have violations"() {

        def input = new CreateManagerUserProfileInput(
                cpf: cpf,
                firstName: firstName,
                lastName: lastName,
                username: username,
                phone: null,
                address: addressInput
        )

        def violations = validator.validate(input)

        assert violations.isEmpty()

    }

    @Test
    void "WHEN validate create manager user profile input with empty phone, THEN should have violations"() {

        def input = new CreateManagerUserProfileInput(
                cpf: cpf,
                firstName: firstName,
                lastName: lastName,
                username: username,
                phone: "",
                address: addressInput
        )

        def violations = validator.validate(input)

        assert !violations.isEmpty()

    }

    @Test
    void "WHEN validate create manager user profile input with phone greater than 50 characters, THEN should have violations"() {

        def input = new CreateManagerUserProfileInput(
                cpf: cpf,
                firstName: firstName,
                lastName: lastName,
                username: username,
                phone: faker.lorem().characters(51),
                address: addressInput
        )

        def violations = validator.validate(input)

        assert !violations.isEmpty()

    }


    @Test
    void "WHEN validate create manager user profile input with null address, THEN should have violations"() {

        def input = new CreateManagerUserProfileInput(
                cpf: cpf,
                firstName: firstName,
                lastName: lastName,
                username: username,
                phone: phone,
                address: null
        )

        def violations = validator.validate(input)

        assert violations.isEmpty()

    }

    @Test
    void "WHEN validate create manager user profile input with an invalid address, THEN should have violations"() {

        def input = new CreateManagerUserProfileInput(
                cpf: cpf,
                firstName: firstName,
                lastName: lastName,
                username: username,
                phone: phone,
                address: new AddressInput()
        )

        def violations = validator.validate(input)

        assert !violations.isEmpty()

    }

}
