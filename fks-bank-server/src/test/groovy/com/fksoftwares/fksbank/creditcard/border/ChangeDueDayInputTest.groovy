package com.fksoftwares.fksbank.creditcard.border

import com.fksoftwares.fksbank.creditcard.web.input.ChangeDueDayInput
import org.junit.jupiter.api.Test

import javax.validation.Validation
import javax.validation.Validator
import javax.validation.ValidatorFactory

class ChangeDueDayInputTest {

    private ValidatorFactory factory = Validation.buildDefaultValidatorFactory()
    private Validator validator = factory.getValidator()

    @Test
    void "WHEN validate change due day input with all attributes valid, THEN should not have violations"() {

        def input = new ChangeDueDayInput(
                dueDay: 20
        )

        def violations = validator.validate(input)

        assert violations.isEmpty()

    }

    @Test
    void "WHEN validate change due day input with null due day, THEN should have violations"() {

        def input = new ChangeDueDayInput(
                dueDay: null
        )

        def violations = validator.validate(input)

        assert !violations.isEmpty()

    }

}
