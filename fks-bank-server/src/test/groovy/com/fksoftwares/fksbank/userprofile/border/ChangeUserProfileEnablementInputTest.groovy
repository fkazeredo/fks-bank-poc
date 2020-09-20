package com.fksoftwares.fksbank.userprofile.border

import com.fksoftwares.fksbank.userprofile.web.input.ChangeUserProfileEnablementInput
import com.github.javafaker.Faker
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import javax.validation.Validation
import javax.validation.Validator
import javax.validation.ValidatorFactory

class ChangeUserProfileEnablementInputTest {

    private ValidatorFactory factory = Validation.buildDefaultValidatorFactory()
    private Validator validator = factory.getValidator()

    private Faker faker = new Faker()

    private Boolean enablement

    @BeforeEach
    void setUp(){
        enablement = Boolean.valueOf(String.valueOf(faker.number().numberBetween(0, 1)))
    }

    @Test
    void "WHEN validate change user profile enablement input with all attributes valid, THEN should not have violations"() {

        def input = new ChangeUserProfileEnablementInput(
                enablement: enablement
        )

        def violations = validator.validate(input)

       assert violations.isEmpty()

    }

    @Test
    void "WHEN validate change user profile enablement input with null enablement, THEN should have violations"() {

        def input = new ChangeUserProfileEnablementInput()

        def violations = validator.validate(input)

       assert !violations.isEmpty()

    }

}
