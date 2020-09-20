package com.fksoftwares.fksbank.userprofile.border

import com.fksoftwares.fksbank.userprofile.web.input.ApproveUserProfileInput
import com.github.javafaker.Faker
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import javax.validation.Validation
import javax.validation.Validator
import javax.validation.ValidatorFactory

import static org.junit.jupiter.api.Assertions.assertFalse

class ApproveUserProfileInputTest {

    private ValidatorFactory factory = Validation.buildDefaultValidatorFactory()
    private Validator validator = factory.getValidator()

    private Faker faker = new Faker()

    private BigDecimal maxLimit

    @BeforeEach
    void setUp(){
        maxLimit = new BigDecimal(faker.number().randomNumber())
    }

    @Test
    void "WHEN validate approve user profile input with all attributes valid, THEN should not have violations"() {

        def input = new ApproveUserProfileInput(maxLimit: maxLimit)

        def violations = validator.validate(input)

       assert violations.isEmpty()

    }

    @Test
    void "WHEN validate approve user profile input with null max limit, THEN should have violations"() {

        def input = new ApproveUserProfileInput()

        def violations = validator.validate(input)

        assertFalse(violations.isEmpty())

    }

}
