package com.fksoftwares.fksbank.integration.web

import com.fksoftwares.fksbank.creditcard.InvoiceRepository
import com.fksoftwares.fksbank.creditcard.web.model.InvoiceModel
import com.fksoftwares.fksbank.creditcard.web.input.PaymentInput
import com.fksoftwares.fksbank.integration.UserRequestFactory
import io.restassured.RestAssured
import org.flywaydb.core.Flyway
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.test.context.ActiveProfiles

import java.time.LocalDate

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class InvoiceResourceIT {

    private static final String BASE_URL = "/credit-cards"

    @LocalServerPort
    Integer port

    @Autowired
    private Flyway flyway

    @Autowired
    private InvoiceRepository invoiceRepository

    @BeforeEach
    void setUp() {
        RestAssured.port = port
        flyway.clean()
        flyway.migrate()
    }

    @Test
    void "GIVEN a customer user profile, WHEN find all invoices of a credit card, THEN should return '200' success response and a list of invoice's summaries"() {

        def creditCardId = 1L

        def invoices = UserRequestFactory.withCustomer()
                .when()
                .get("${BASE_URL}/${creditCardId}/invoices")
                .then()
                .statusCode(200).extract().as(List)

        assert invoices != null
        assert invoices.size() == 4

        assert invoices[0]["month"] == 2
        assert invoices[0]["year"] == 2020
        assert invoices[0]["total"] == -352.54

        assert invoices[1]["month"] == 3
        assert invoices[1]["year"] == 2020
        assert invoices[1]["total"] == -96.15

        assert invoices[2]["month"] == 4
        assert invoices[2]["year"] == 2020
        assert invoices[2]["total"] == -20.25

    }

    @Test
    void "GIVEN a manager user profile, WHEN find all invoices of a credit card, THEN should return '403' error response"() {

        def creditCardId = 1L

        UserRequestFactory.withManager()
                .when()
                .get("${BASE_URL}/${creditCardId}/invoices")
                .then()
                .statusCode(403)

    }

    @Test
    void "GIVEN a customer user profile, WHEN find all invoices of another user profile credit card, THEN should return '403' error response"() {

        def creditCardId = 2L

        UserRequestFactory.withCustomer()
                .when()
                .get("${BASE_URL}/${creditCardId}/invoices")
                .then()
                .statusCode(403)

    }

    @Test
    void "GIVEN a customer user profile, WHEN find a invoice by credit card and invoice id, THEN should return '200' success response and an invoice with transactions"() {

        def creditCardId = 1L
        def invoiceId = 1L

        def invoice = UserRequestFactory.withCustomer()
                .when()
                .get("${BASE_URL}/${creditCardId}/invoices/${invoiceId}")
                .then()
                .statusCode(200).extract().as(InvoiceModel)


        assert invoice != null
        assert invoice.id == 1
        assert invoice.dueDate == LocalDate.of(2020, 02, 05)
        assert invoice.total == -352.5400
        assert invoice.transactions.size() == 3

    }

    @Test
    void "GIVEN a manager user profile, WHEN find a invoice by credit card and invoice id, THEN should return '403' error response"() {

        def creditCardId = 1L
        def invoiceId = 1L

        UserRequestFactory.withManager()
                .when()
                .get("${BASE_URL}/${creditCardId}/invoices/${invoiceId}")
                .then()
                .statusCode(403)

    }

    @Test
    void "GIVEN a customer user profile, WHEN find a invoice by credit card and invoice id of another user profile credit card, THEN should return '403' error response"() {

        def creditCardId = 2L
        def invoiceId = 10L

        UserRequestFactory.withCustomer()
                .when()
                .get("${BASE_URL}/${creditCardId}/invoices/${invoiceId}")
                .then()
                .statusCode(403)

    }

    @Test
    void "GIVEN a customer user profile, WHEN find a invoice by a credit that doesn't exists, THEN should return '404' error response"() {

        def creditCardId = Long.MAX_VALUE
        def invoiceId = 1L

        UserRequestFactory.withCustomer()
                .when()
                .get("${BASE_URL}/${creditCardId}/invoices/${invoiceId}")
                .then()
                .statusCode(404)

    }

    @Test
    void "GIVEN a customer user profile, WHEN find a invoice by an invoice id that doesn't exists, THEN should return '404' error response"() {

        def creditCardId = 1L
        def invoiceId = Long.MAX_VALUE

        UserRequestFactory.withCustomer()
                .when()
                .get("${BASE_URL}/${creditCardId}/invoices/${invoiceId}")
                .then()
                .statusCode(404)

    }

    @Test
    void "GIVEN a customer user profile, WHEN pay, THEN should return '204' success response and an invoice with transactions"() {

        def creditCardId = 1L
        def invoiceId = 9L

        def input = new PaymentInput(
                value: 900.00
        )

        def invoice = UserRequestFactory.withCustomer()
                .body(input)
                .when()
                .post("${BASE_URL}/${creditCardId}/invoices/${invoiceId}/payment")
                .then()
                .statusCode(201).extract().as(InvoiceModel)


        assert invoice != null
        assert invoice.id == 9
        assert invoice.dueDate == LocalDate.of(2020, 10, 05)
        assert invoice.total == 0
        assert invoice.transactions.size() == 2

    }


    @Test
    void "GIVEN a manager user profile, WHEN pay, THEN should return '403' error response"() {

        def creditCardId = 1L
        def invoiceId = 9L

        def input = new PaymentInput(
                value: 900.00
        )

        UserRequestFactory.withManager()
                .body(input)
                .when()
                .post("${BASE_URL}/${creditCardId}/invoices/${invoiceId}/payment")
                .then()
                .statusCode(403)

    }

    @Test
    void "GIVEN a customer user profile, WHEN pay a invoice by credit card and invoice id of another user profile credit card, THEN should return '403' error response"() {

        def creditCardId = 2L
        def invoiceId = 10L

        def input = new PaymentInput(
                value: 900.00
        )

        UserRequestFactory.withCustomer()
                .body(input)
                .when()
                .post("${BASE_URL}/${creditCardId}/invoices/${invoiceId}/payment")
                .then()
                .statusCode(403)

    }

    @Test
    void "GIVEN a customer user profile, WHEN pay a invoice by a credit that doesn't exists, THEN should return '404' error response"() {

        def creditCardId = Long.MAX_VALUE
        def invoiceId = 1L

        def input = new PaymentInput(
                value: 900.00
        )

        UserRequestFactory.withCustomer()
                .body(input)
                .when()
                .post("${BASE_URL}/${creditCardId}/invoices/${invoiceId}/payment")
                .then()
                .statusCode(404)

    }

    @Test
    void "GIVEN a customer user profile, WHEN pay a invoice by an invoice id that doesn't exists, THEN should return '404' error response"() {

        def creditCardId = 1L
        def invoiceId = Long.MAX_VALUE

        def input = new PaymentInput(
                value: 900.00
        )

        UserRequestFactory.withCustomer()
                .body(input)
                .when()
                .post("${BASE_URL}/${creditCardId}/invoices/${invoiceId}/payment")
                .then()
                .statusCode(404)

    }

}
