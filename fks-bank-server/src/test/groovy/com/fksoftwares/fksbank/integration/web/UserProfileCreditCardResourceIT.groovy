package com.fksoftwares.fksbank.integration.web

import com.fksoftwares.fksbank.creditcard.Category
import com.fksoftwares.fksbank.creditcard.InvoiceTransaction
import com.fksoftwares.fksbank.creditcard.Purchase
import com.fksoftwares.fksbank.creditcard.web.model.CreditCardSummaryModel
import com.fksoftwares.fksbank.integration.UserRequestFactory
import io.restassured.RestAssured
import org.flywaydb.core.Flyway
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.test.context.ActiveProfiles

import java.time.LocalDateTime

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class UserProfileCreditCardResourceIT {

    @LocalServerPort
    Integer port

    @Autowired
    private Flyway flyway

    @Autowired
    private Purchase purchase

    @BeforeEach
    void setUp() {
        RestAssured.port = port
        flyway.clean()
        flyway.migrate()
    }

    @Test
    void "GIVEN a customer user profile, WHEN find his own credit card, THEN should return '200' with credit card summary"() {

        def userProfileId = 2L

        def creditCardSummary = UserRequestFactory.withCustomer()
                .when()
                .get("/user-profiles/${userProfileId}/credit-cards")
                .then()
                .statusCode(200).extract().as(CreditCardSummaryModel)

        assert creditCardSummary != null
        assert creditCardSummary.creditCardLastNumbers == '9550'
        assert creditCardSummary.currentInvoiceTotal == -900
        assert creditCardSummary.availableLimit == 1131.0600

    }

    @Test
    void "GIVEN a customer user profile, WHEN find his own credit card after make a purchase, THEN should return '200' with updated credit card summary"() {

        def userProfileId = 2L

        addAPurchaseToCurrentInvoice()

        def creditCardSummary = UserRequestFactory.withCustomer()
                .when()
                .get("/user-profiles/${userProfileId}/credit-cards")
                .then()
                .statusCode(200).extract().as(CreditCardSummaryModel)

        assert creditCardSummary != null
        assert creditCardSummary.creditCardLastNumbers == '9550'
        assert creditCardSummary.currentInvoiceTotal == -1500.00
        assert creditCardSummary.availableLimit == 531.0600

    }

    void addAPurchaseToCurrentInvoice(){
        purchase.execute(1, 0, false, new InvoiceTransaction(
                Category.EDUCATION,
                LocalDateTime.now(),
                "Relógio de pulso",
                new BigDecimal("-600")
        ))
    }


}
