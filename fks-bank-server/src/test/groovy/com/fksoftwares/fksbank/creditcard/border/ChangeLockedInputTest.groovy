package com.fksoftwares.fksbank.creditcard.border

import com.fksoftwares.fksbank.creditcard.web.input.ChangeLockedInput
import org.junit.jupiter.api.Test

import javax.validation.Validation
import javax.validation.Validator
import javax.validation.ValidatorFactory

class ChangeLockedInputTest {

    private ValidatorFactory factory = Validation.buildDefaultValidatorFactory()
    private Validator validator = factory.getValidator()

    @Test
    void "WHEN validate change locked input with all attributes valid, THEN should not have violations"() {

        def input = new ChangeLockedInput(
                locked: Boolean.FALSE
        )

        def violations = validator.validate(input)

        assert violations.isEmpty()

    }

    @Test
    void "WHEN validate change locked input with null locked, THEN should have violations"() {

        def input = new ChangeLockedInput(
                locked: null
        )

        def violations = validator.validate(input)

        assert !violations.isEmpty()

    }

}
