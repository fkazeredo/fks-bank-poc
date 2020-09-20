package com.fksoftwares.fksbank.integration.queue

import com.fksoftwares.fksbank.core.queue.EventPublisher
import com.fksoftwares.fksbank.creditcard.InvoiceRepository
import com.fksoftwares.fksbank.creditcard.Purchase
import com.fksoftwares.fksbank.creditcard.PurchaseCreated
import io.restassured.RestAssured
import org.flywaydb.core.Flyway
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.SpyBean
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.test.context.ActiveProfiles

import java.time.LocalDateTime

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class InvoiceConsumerIT {

    @LocalServerPort
    Integer port

    @Autowired
    private Flyway flyway

    @Autowired
    private InvoiceRepository invoiceRepository

    @Autowired
    EventPublisher eventPublisher

    @SpyBean
    private Purchase purchase

    @Value("\${spring.rabbitmq.queue.creditcard-purchase.name}")
    private String exchangeName

    @BeforeEach
    void setUp() {
        RestAssured.port = port
        flyway.clean()
        flyway.migrate()
    }

    @Test
    void "GIVEN a purchase transaction sent by a client, WHEN listened by consumer, THEN purchase should be executed"() {

        def message = new PurchaseCreated(
                creditCardId: 1,
                installments: 12,
                hasInstallmentsInterest: true,
                category: "OTHERS",
                date: LocalDateTime.now(),
                description: "Curso secreto",
                value: -20.0
        )

        def creditCardId = message.creditCardId

        def invoice = invoiceRepository.findCurrentByCreditCardId(creditCardId).get()

        def oldSize = invoice.transactions.size()

        eventPublisher.send(exchangeName, message)

        Thread.sleep(5000)

        invoice = invoiceRepository.findCurrentByCreditCardId(creditCardId).get()

        def newSize = invoice.transactions.size()

        assert oldSize != newSize

    }

}
