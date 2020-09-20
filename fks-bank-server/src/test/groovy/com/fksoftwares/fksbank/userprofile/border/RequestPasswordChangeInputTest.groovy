package com.fksoftwares.fksbank.userprofile.border

import com.fksoftwares.fksbank.userprofile.web.input.RequestPasswordChangeInput
import com.github.javafaker.Faker
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import javax.validation.Validation
import javax.validation.Validator
import javax.validation.ValidatorFactory

class RequestPasswordChangeInputTest {

    private ValidatorFactory factory = Validation.buildDefaultValidatorFactory()
    private Validator validator = factory.getValidator()

    private Faker faker = new Faker()

    private String mail

    @BeforeEach
    void setUp() {
        mail = faker.internet().emailAddress()
    }

    @Test
    void "WHEN validate request password change input with all attributes valid, THEN should not have violations"() {

        def input = new RequestPasswordChangeInput(
                mail: mail
        )

        def violations = validator.validate(input)

        assert violations.isEmpty()

    }

    @Test
    void "WHEN validate request password change input with an invalid mail, THEN should have violations"() {

        def input = new RequestPasswordChangeInput(
                mail: "admin.com"
        )

        def violations = validator.validate(input)

        assert !violations.isEmpty()

    }

    @Test
    void "WHEN validate request password change input with null mail, THEN should have violations"() {

        def input = new RequestPasswordChangeInput()

        def violations = validator.validate(input)

        assert !violations.isEmpty()

    }

    @Test
    void "WHEN validate request password change input with empty mail, THEN should have violations"() {

        def input = new RequestPasswordChangeInput(mail: "")

        def violations = validator.validate(input)

        assert !violations.isEmpty()

    }

}
