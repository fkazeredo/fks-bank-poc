package com.fksoftwares.fksbank.creditcard.border

import com.fksoftwares.fksbank.creditcard.web.input.AdjustCurrentLimitInput
import org.junit.jupiter.api.Test

import javax.validation.Validation
import javax.validation.Validator
import javax.validation.ValidatorFactory

class AdjustCurrentLimitInputTest {

    private ValidatorFactory factory = Validation.buildDefaultValidatorFactory()
    private Validator validator = factory.getValidator()

    @Test
    void "WHEN validate adjust current limit input with all attributes valid, THEN should not have violations"() {

        def input = new AdjustCurrentLimitInput(
                currentLimit: new BigDecimal("20.")
        )

        def violations = validator.validate(input)

        assert violations.isEmpty()

    }

    @Test
    void "WHEN validate adjust current limit input with null current limit, THEN should have violations"() {

        def input = new AdjustCurrentLimitInput(
                currentLimit: null
        )

        def violations = validator.validate(input)

        assert !violations.isEmpty()

    }

}
