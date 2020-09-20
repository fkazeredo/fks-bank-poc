package com.fksoftwares.fksbank.integration.web

import com.fksoftwares.fksbank.creditcard.CreditCardRepository
import com.fksoftwares.fksbank.creditcard.web.input.AdjustCurrentLimitInput
import com.fksoftwares.fksbank.creditcard.web.input.ChangeDueDayInput
import com.fksoftwares.fksbank.creditcard.web.input.ChangeLockedInput
import com.fksoftwares.fksbank.integration.UserRequestFactory
import io.restassured.RestAssured
import org.flywaydb.core.Flyway
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.test.context.ActiveProfiles

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class CreditCardResourceIT {

    private static final String BASE_URL = "/credit-cards"

    @LocalServerPort
    Integer port

    @Autowired
    private Flyway flyway

    @Autowired
    private CreditCardRepository creditCardRepository

    @BeforeEach
    void setUp() {
        RestAssured.port = port
        flyway.clean()
        flyway.migrate()
    }

    @Test
    void "GIVEN a customer user profile, WHEN adjust current limit, THEN should return '204' success response and current limit should change"() {

        def creditCardId = 1L

        def input = new AdjustCurrentLimitInput(
                currentLimit: new BigDecimal("3000.")
        )

        def oldLimit = creditCardRepository.findById(creditCardId).get().currentLimit

        UserRequestFactory.withCustomer()
                .body(input)
                .when()
                .patch("${BASE_URL}/${creditCardId}/current-limit")
                .then()
                .statusCode(204)

        def newLimit = creditCardRepository.findById(creditCardId).get().currentLimit

        assert oldLimit != newLimit

    }

    @Test
    void "GIVEN a customer user profile, WHEN adjust another customer current limit, THEN should return '403' error response"() {

        def creditCardId = 2L

        def input = new AdjustCurrentLimitInput(
                currentLimit: new BigDecimal("3000.")
        )

        UserRequestFactory.withCustomer()
                .body(input)
                .when()
                .patch("${BASE_URL}/${creditCardId}/current-limit")
                .then()
                .statusCode(403)

    }

    @Test
    void "GIVEN a customer user profile, WHEN adjust current limit of a credit card that doesn't exists, THEN should return '404' error response"() {

        def creditCardId = 5L

        def input = new AdjustCurrentLimitInput(
                currentLimit: new BigDecimal("3000.")
        )

        UserRequestFactory.withCustomer()
                .body(input)
                .when()
                .patch("${BASE_URL}/${creditCardId}/current-limit")
                .then()
                .statusCode(404)

    }

    @Test
    void "GIVEN a client passing an invalid input, WHEN adjust current limit, THEN should return '400' error response"() {

        def creditCardId = 5L

        def input = "{}"

        UserRequestFactory.withCustomer()
                .body(input)
                .when()
                .patch("${BASE_URL}/${creditCardId}/current-limit")
                .then()
                .statusCode(400)

    }

    @Test
    void "GIVEN a manager user profile, WHEN adjust current limit, THEN should return '403' error response"() {

        def creditCardId = 1L

        def input = new AdjustCurrentLimitInput(
                currentLimit: new BigDecimal("3000.")
        )

        UserRequestFactory.withManager()
                .body(input)
                .when()
                .patch("${BASE_URL}/${creditCardId}/current-limit")
                .then()
                .statusCode(403)

    }

    @Test
    void "GIVEN a customer user profile, WHEN change due day, THEN should return '204' success response and current limit should change"() {

        def creditCardId = 1L

        def input = new ChangeDueDayInput(
                dueDay: 20
        )

        def oldDueDay = creditCardRepository.findById(creditCardId).get().dueDay

        UserRequestFactory.withCustomer()
                .body(input)
                .when()
                .patch("${BASE_URL}/${creditCardId}/due-day")
                .then()
                .statusCode(204)

        def newDueDay = creditCardRepository.findById(creditCardId).get().dueDay

        assert oldDueDay != newDueDay

    }

    @Test
    void "GIVEN a customer user profile, WHEN change another customer due day, THEN should return '403' error response"() {

        def creditCardId = 2L

        def input = new ChangeDueDayInput(
                dueDay: 20
        )

        UserRequestFactory.withCustomer()
                .body(input)
                .when()
                .patch("${BASE_URL}/${creditCardId}/due-day")
                .then()
                .statusCode(403)

    }

    @Test
    void "GIVEN a customer user profile, WHEN change due day of a credit card that doesn't exists, THEN should return '404' error response"() {

        def creditCardId = 5L

        def input = new ChangeDueDayInput(
                dueDay: 20
        )

        UserRequestFactory.withCustomer()
                .body(input)
                .when()
                .patch("${BASE_URL}/${creditCardId}/due-day")
                .then()
                .statusCode(404)

    }

    @Test
    void "GIVEN a client passing an invalid input, WHEN change due day, THEN should return '400' error response"() {

        def creditCardId = 5L

        def input = "{}"

        UserRequestFactory.withCustomer()
                .body(input)
                .when()
                .patch("${BASE_URL}/${creditCardId}/due-day")
                .then()
                .statusCode(400)

    }

    @Test
    void "GIVEN a manager user profile, WHEN change due day, THEN should return '403' error response"() {

        def creditCardId = 1L

        def input = new ChangeDueDayInput(
                dueDay: 20
        )

        UserRequestFactory.withManager()
                .body(input)
                .when()
                .patch("${BASE_URL}/${creditCardId}/due-day")
                .then()
                .statusCode(403)

    }

    @Test
    void "GIVEN a customer user profile, WHEN change locked, THEN should return '204' success response and current limit should change"() {

        def creditCardId = 1L

        def input = new ChangeLockedInput(
                locked: Boolean.TRUE
        )

        def oldLocked = creditCardRepository.findById(creditCardId).get().locked

        UserRequestFactory.withCustomer()
                .body(input)
                .when()
                .patch("${BASE_URL}/${creditCardId}/locked")
                .then()
                .statusCode(204)

        def newLocked = creditCardRepository.findById(creditCardId).get().locked

        assert oldLocked != newLocked

    }

    @Test
    void "GIVEN a customer user profile, WHEN change another customer locked, THEN should return '403' error response"() {

        def creditCardId = 2L

        def input = new ChangeLockedInput(
                locked: Boolean.TRUE
        )

        UserRequestFactory.withCustomer()
                .body(input)
                .when()
                .patch("${BASE_URL}/${creditCardId}/locked")
                .then()
                .statusCode(403)

    }

    @Test
    void "GIVEN a customer user profile, WHEN change locked of a credit card that doesn't exists, THEN should return '404' error response"() {

        def creditCardId = 5L

        def input = new ChangeLockedInput(
                locked: Boolean.TRUE
        )

        UserRequestFactory.withCustomer()
                .body(input)
                .when()
                .patch("${BASE_URL}/${creditCardId}/locked")
                .then()
                .statusCode(404)

    }

    @Test
    void "GIVEN a client passing an invalid input, WHEN change locked, THEN should return '400' error response"() {

        def creditCardId = 5L

        def input = "{}"

        UserRequestFactory.withCustomer()
                .body(input)
                .when()
                .patch("${BASE_URL}/${creditCardId}/locked")
                .then()
                .statusCode(400)

    }

    @Test
    void "GIVEN a manager user profile, WHEN change locked, THEN should return '403' error response"() {

        def creditCardId = 1L

        def input = new ChangeLockedInput(
                locked: Boolean.TRUE
        )

        UserRequestFactory.withManager()
                .body(input)
                .when()
                .patch("${BASE_URL}/${creditCardId}/locked")
                .then()
                .statusCode(403)

    }

}
