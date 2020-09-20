package com.fksoftwares.fksbank.creditcard.border

import com.fksoftwares.fksbank.creditcard.web.input.PaymentInput
import org.junit.jupiter.api.Test

import javax.validation.Validation
import javax.validation.Validator
import javax.validation.ValidatorFactory

class PaymentInputTest {

    private ValidatorFactory factory = Validation.buildDefaultValidatorFactory()
    private Validator validator = factory.getValidator()

    @Test
    void "WHEN validate payment input with all attributes valid, THEN should not have violations"() {

        def input = new PaymentInput(
                value: new BigDecimal("20.")
        )

        def violations = validator.validate(input)

        assert violations.isEmpty()

    }

    @Test
    void "WHEN validate payment input with null value, THEN should have violations"() {

        def input = new PaymentInput(
                value: null
        )

        def violations = validator.validate(input)

        assert !violations.isEmpty()

    }

}
