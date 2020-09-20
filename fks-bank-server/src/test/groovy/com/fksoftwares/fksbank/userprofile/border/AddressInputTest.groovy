package com.fksoftwares.fksbank.userprofile.border

import com.fksoftwares.fksbank.userprofile.web.input.AddressInput
import com.github.javafaker.Faker
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import javax.validation.Validation
import javax.validation.Validator
import javax.validation.ValidatorFactory

class AddressInputTest {

    private ValidatorFactory factory = Validation.buildDefaultValidatorFactory()
    private Validator validator = factory.getValidator()

    private Faker faker = new Faker()
    private String zipCode
    private String street
    private String number
    private String complement
    private String neighborhood
    private String city

    @BeforeEach
    void setUp() {
        zipCode = faker.number().digits(8)
        street = faker.address().streetName()
        number = faker.number().digits(2)
        complement = faker.lorem().fixedString(50)
        neighborhood = faker.lorem().fixedString(50)
        city = faker.address().cityName()
    }

    @Test
    void "WHEN validate address input with all attributes valid, THEN should not have violations"() {

        def input = new AddressInput(
                zipCode: zipCode,
                street: street,
                number: number,
                complement: complement,
                neighborhood: neighborhood,
                city: city
        )

        def violations = validator.validate(input)

        assert violations.isEmpty()

    }

    @Test
    void "WHEN validate address input with null zip code, THEN should have violations"() {

        def input = new AddressInput(
                zipCode: null,
                street: street,
                number: number,
                complement: complement,
                neighborhood: neighborhood,
                city: city
        )

        def violations = validator.validate(input)

        assert !violations.isEmpty()

    }

    @Test
    void "WHEN validate address input with empty zip code, THEN should have violations"() {

        def input = new AddressInput(
                zipCode: "",
                street: street,
                number: number,
                complement: complement,
                neighborhood: neighborhood,
                city: city
        )

        def violations = validator.validate(input)

        assert !violations.isEmpty()

    }

    @Test
    void "WHEN validate address input with zip code greater than 150 characters, THEN should have violations"() {

        def input = new AddressInput(
                zipCode: faker.lorem().fixedString(151),
                street: street,
                number: number,
                complement: complement,
                neighborhood: neighborhood,
                city: city
        )

        def violations = validator.validate(input)

        assert !violations.isEmpty()

    }

    @Test
    void "WHEN validate address input with null street, THEN should have violations"() {


        def input = new AddressInput(
                zipCode: zipCode,
                street: null,
                number: number,
                complement: complement,
                neighborhood: neighborhood,
                city: city
        )

        def violations = validator.validate(input)

        assert !violations.isEmpty()

    }

    @Test
    void "WHEN validate address input with empty street, THEN should have violations"() {

        def input = new AddressInput(
                zipCode: zipCode,
                street: "",
                number: number,
                complement: complement,
                neighborhood: neighborhood,
                city: city
        )

        def violations = validator.validate(input)

        assert !violations.isEmpty()

    }

    @Test
    void "WHEN validate address input with street greater than 255 characters, THEN should have violations"() {

        def input = new AddressInput(
                zipCode: zipCode,
                street: faker.lorem().fixedString(256),
                number: number,
                complement: complement,
                neighborhood: neighborhood,
                city: city
        )

        def violations = validator.validate(input)

        assert !violations.isEmpty()

    }

    @Test
    void "WHEN validate address input with null number, THEN should have violations"() {

        def input = new AddressInput(
                zipCode: zipCode,
                street: street,
                number: null,
                complement: complement,
                neighborhood: neighborhood,
                city: city
        )

        def violations = validator.validate(input)

        assert !violations.isEmpty()

    }

    @Test
    void "WHEN validate address input with empty number, THEN should have violations"() {

        def input = new AddressInput(
                zipCode: zipCode,
                street: street,
                number: "",
                complement: complement,
                neighborhood: neighborhood,
                city: city
        )

        def violations = validator.validate(input)

        assert !violations.isEmpty()

    }

    @Test
    void "WHEN validate address input with number greater than 50 characters, THEN should have violations"() {


        def input = new AddressInput(
                zipCode: zipCode,
                street: street,
                number: faker.lorem().fixedString(51),
                complement: complement,
                neighborhood: neighborhood,
                city: city
        )

        def violations = validator.validate(input)

        assert !violations.isEmpty()

    }

    @Test
    void "WHEN validate address input with null complement, THEN should not have violations"() {

        def input = new AddressInput(
                zipCode: zipCode,
                street: street,
                number: number,
                complement: null,
                neighborhood: neighborhood,
                city: city
        )

        def violations = validator.validate(input)

        assert violations.isEmpty()

    }

    @Test
    void "WHEN validate address input with empty complement, THEN THEN should have violations"() {

        def input = new AddressInput(
                zipCode: zipCode,
                street: street,
                number: number,
                complement: "",
                neighborhood: neighborhood,
                city: city
        )

        def violations = validator.validate(input)

        assert !violations.isEmpty()

    }

    @Test
    void "WHEN validate address input with complement greater than 150 characters, THEN should have violations"() {

        def input = new AddressInput(
                zipCode: zipCode,
                street: street,
                number: number,
                complement: faker.lorem().fixedString(151),
                neighborhood: neighborhood,
                city: city
        )

        def violations = validator.validate(input)

        assert !violations.isEmpty()

    }

    @Test
    void "WHEN address input neighborhood is null, THEN should have violations"() {

        def input = new AddressInput(
                zipCode: zipCode,
                street: street,
                number: number,
                complement: complement,
                neighborhood: null,
                city: city
        )

        def violations = validator.validate(input)

        assert !violations.isEmpty()

    }

    @Test
    void "WHEN address input neighborhood is empty, THEN should have violations"() {

        def input = new AddressInput(
                zipCode: zipCode,
                street: street,
                number: number,
                complement: complement,
                neighborhood: "",
                city: city
        )

        def violations = validator.validate(input)

        assert !violations.isEmpty()

    }

    @Test
    void "WHEN address input neighborhood is greater than 150 characters, THEN should have violations"() {

        def input = new AddressInput(
                zipCode: zipCode,
                street: street,
                number: number,
                complement: complement,
                neighborhood: faker.lorem().fixedString(151),
                city: city
        )

        def violations = validator.validate(input)

        assert !violations.isEmpty()

    }

    @Test
    void "WHEN address input city is null, THEN should have violations"() {

        def input = new AddressInput(
                zipCode: zipCode,
                street: street,
                number: number,
                complement: complement,
                neighborhood: neighborhood,
                city: null
        )

        def violations = validator.validate(input)

        assert !violations.isEmpty()

    }

    @Test
    void "WHEN address input city is empty, THEN should have violations"() {

        def input = new AddressInput(
                zipCode: zipCode,
                street: street,
                number: number,
                complement: complement,
                neighborhood: neighborhood,
                city: null
        )

        def violations = validator.validate(input)

        assert !violations.isEmpty()

    }

    @Test
    void "WHEN address input city is greater than 150 characters, THEN should have violations"() {

        def input = new AddressInput(
                zipCode: zipCode,
                street: street,
                number: number,
                complement: complement,
                neighborhood: neighborhood,
                city: faker.lorem().fixedString(151)
        )

        def violations = validator.validate(input)

        assert !violations.isEmpty()

    }


}
