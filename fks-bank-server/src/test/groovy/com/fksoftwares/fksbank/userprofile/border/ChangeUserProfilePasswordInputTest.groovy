package com.fksoftwares.fksbank.userprofile.border

import com.fksoftwares.fksbank.userprofile.web.input.ChangeUserProfilePasswordInput
import com.github.javafaker.Faker
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import javax.validation.Validation
import javax.validation.Validator
import javax.validation.ValidatorFactory

class ChangeUserProfilePasswordInputTest {

    private ValidatorFactory factory = Validation.buildDefaultValidatorFactory()
    private Validator validator = factory.getValidator()

    private Faker faker
    private String password
    private String passwordConfirmation

    @BeforeEach
    void setUp() {
        faker = new Faker()
        password = faker.internet().password(true)
        passwordConfirmation = faker.internet().password(true)
    }

    @Test
    void "WHEN validate change user profile password input with all attributes valid, THEN should not have violations"() {

        def input = new ChangeUserProfilePasswordInput(
                password: password,
                passwordConfirmation: passwordConfirmation
        )

        def violations = validator.validate(input)

       assert violations.isEmpty()

    }

    @Test
    void "WHEN validate change user profile password input with null password value, THEN should have violations"() {

        def input = new ChangeUserProfilePasswordInput(
                password: null,
                passwordConfirmation: passwordConfirmation
        )

        def violations = validator.validate(input)

        assert !violations.isEmpty()

    }

    @Test
    void "WHEN validate change user profile password input with empty password value, THEN should have violations"() {

        def input = new ChangeUserProfilePasswordInput(
                password: "",
                passwordConfirmation: passwordConfirmation
        )

        def violations = validator.validate(input)

        assert !violations.isEmpty()

    }

    @Test
    void "WHEN validate change user profile password input with password value greater than 255 characters, THEN should have violations"() {

        def input = new ChangeUserProfilePasswordInput(
                password: faker.lorem().fixedString(256),
                passwordConfirmation: passwordConfirmation
        )

        def violations = validator.validate(input)

        assert !violations.isEmpty()

    }

    @Test
    void "WHEN validate change user profile password input with null password confirmation, THEN should have violations"() {

        def input = new ChangeUserProfilePasswordInput(
                password: password,
                passwordConfirmation: null
        )

        def violations = validator.validate(input)

        assert !violations.isEmpty()

    }

    @Test
    void "WHEN validate change user profile password input with empty password confirmation, THEN should have violations"() {

        def input = new ChangeUserProfilePasswordInput(
                password: password,
                passwordConfirmation: ""
        )

        def violations = validator.validate(input)

        assert !violations.isEmpty()

    }

    @Test
    void "WHEN validate change user profile password input with password confirmation greater than 255 characters, THEN should have violations"() {

        def input = new ChangeUserProfilePasswordInput()
        input.setPassword(password)
        input.setPasswordConfirmation(faker.lorem().fixedString(256))

        def violations = validator.validate(input)

        assert !violations.isEmpty()

    }

}
