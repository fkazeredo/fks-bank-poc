package com.fksoftwares.fksbank.creditcard

import com.fksoftwares.fksbank.core.BusinessException
import com.github.javafaker.Faker
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime

import static org.junit.jupiter.api.Assertions.assertThrows

class InvoiceTest {

    private Faker faker = new Faker()

    private CreditCard creditCard

    @BeforeEach
    void setUp() {
        this.creditCard = new CreditCard(1L, faker.name().fullName(), new BigDecimal("500."))
        this.creditCard.unlock()
    }

    @Test
    void "WHEN create an invoice, THEN it should not have transactions"() {

        def invoice = Invoice.createByCreditCard(this.creditCard)
        assert invoice.transactions != null
        assert invoice.transactions.size() == 0

    }

    @Test
    void "WHEN create an invoice by credit card, THEN invoice's credit id is should be equal credit card id"() {

        def invoice = Invoice.createByCreditCard(this.creditCard)

        assert this.creditCard.id == invoice.creditCard.id

    }

    @Test
    void "WHEN create an invoice by credit card, THEN due date should not be SATURDAY or SUNDAY"() {

        def invoice = Invoice.createByCreditCard(this.creditCard)

        assert invoice.dueDate != null
        assert invoice.dueDate.dayOfWeek != DayOfWeek.SATURDAY
        assert invoice.dueDate.dayOfWeek != DayOfWeek.SUNDAY

    }

    @Test
    void "WHEN create an invoice by credit card, THEN closing date should be due date minus 7 days"() {

        def invoice = Invoice.createByCreditCard(this.creditCard)

        assert invoice.closingDate != null
        assert invoice.closingDate < invoice.dueDate
        assert invoice.closingDate == invoice.dueDate.minusDays(7)

    }

    @Test
    void "WHEN create an invoice by credit card, THEN start date should be the closing date minus 1 month plus 1 day"() {

        def invoice = Invoice.createByCreditCard(this.creditCard)

        assert invoice.startDate != null
        assert invoice.startDate == invoice.closingDate.minusMonths(1).plusDays(1)

    }

    @Test
    void "WHEN create an invoice by last invoice, THEN invoice's credit id is should be equal last invoice's credit card id"() {

        def lastInvoice = Invoice.createByCreditCard(this.creditCard)
        def invoice = Invoice.createByLastInvoice(lastInvoice)

        assert invoice.lastInvoice != null
        assert invoice.creditCard.id == lastInvoice.creditCard.id

    }

    @Test
    void "WHEN create an invoice by last invoice, THEN due date should not be SATURDAY or SUNDAY"() {

        def lastInvoice = Invoice.createByCreditCard(this.creditCard)
        def invoice = Invoice.createByLastInvoice(lastInvoice)

        assert invoice.dueDate != null
        assert invoice.dueDate.dayOfWeek != DayOfWeek.SATURDAY
        assert invoice.dueDate.dayOfWeek != DayOfWeek.SUNDAY

    }

    @Test
    void "WHEN create an invoice by last invoice, THEN closing date should be due date minus 7 days"() {

        def lastInvoice = Invoice.createByCreditCard(this.creditCard)
        def invoice = Invoice.createByLastInvoice(lastInvoice)

        assert invoice.closingDate != null
        assert invoice.closingDate < invoice.dueDate
        assert invoice.closingDate == invoice.dueDate.minusDays(7)

    }

    @Test
    void "WHEN create an invoice by last invoice, THEN start date should be the last invoice closing data plus 1 day"() {

        def lastInvoice = Invoice.createByCreditCard(this.creditCard)
        def invoice = Invoice.createByLastInvoice(lastInvoice)

        assert invoice.startDate != null
        assert invoice.startDate == lastInvoice.closingDate.plusDays(1)

    }

    @Test
    void "WHEN add purchase THEN it should be in transactions"() {

        def invoice = Invoice.createByCreditCard(this.creditCard)

        def transaction = new InvoiceTransaction(
                Category.CLOTHING,
                LocalDateTime.now(),
                "2 Blusas básicas - Cia Hering",
                new BigDecimal("-200")
        )

        invoice.addPurchase(transaction)

        assert invoice.transactions != null
        assert invoice.transactions.size() == 1
        assert invoice.transactions.contains(transaction)

    }

    @Test
    void "WHEN add more than 1 purchases THEN it should be in transactions"() {

        def invoice = Invoice.createByCreditCard(this.creditCard)

        def transaction1 = new InvoiceTransaction(
                Category.CLOTHING,
                LocalDateTime.now(),
                "2 Blusas básicas - Cia Hering",
                new BigDecimal("-200")
        )

        def transaction2 = new InvoiceTransaction(
                Category.RESTAURANT,
                LocalDateTime.now(),
                "Burguer King",
                new BigDecimal("-25")
        )

        def transaction3 = new InvoiceTransaction(
                Category.EDUCATION,
                LocalDateTime.now(),
                "Udemy",
                new BigDecimal("-25.60")
        )

        invoice.addPurchase(transaction1)
        invoice.addPurchase(transaction2)
        invoice.addPurchase(transaction3)

        assert invoice.transactions != null
        assert invoice.transactions.size() == 3
        assert invoice.transactions.containsAll(
                [transaction1, transaction2, transaction3]
        )

    }

    @Test
    void "WHEN add purchases THEN total should be the sum of transactions"() {

        def invoice = Invoice.createByCreditCard(this.creditCard)

        def transaction1 = new InvoiceTransaction(
                Category.CLOTHING,
                LocalDateTime.now(),
                "2 Blusas básicas - Cia Hering",
                new BigDecimal("-200")
        )

        def transaction2 = new InvoiceTransaction(
                Category.RESTAURANT,
                LocalDateTime.now(),
                "Burguer King",
                new BigDecimal("-25")
        )

        def transaction3 = new InvoiceTransaction(
                Category.EDUCATION,
                LocalDateTime.now(),
                "Udemy",
                new BigDecimal("-25.60")
        )

        invoice.addPurchase(transaction1)
        invoice.addPurchase(transaction2)
        invoice.addPurchase(transaction3)

        def total = transaction1.value + transaction2.value + transaction3.value

        assert total == invoice.total

    }

    @Test
    void "WHEN add purchase and value of transaction is positive, THEN should throws an exception"() {

        def invoice = Invoice.createByCreditCard(this.creditCard)
        def transaction = new InvoiceTransaction(
                Category.CLOTHING,
                LocalDateTime.now(),
                "2 Blusas básicas - Cia Hering",
                new BigDecimal("1")
        )

        assertThrows(BusinessException, { ->
            invoice.addPurchase(transaction)
        })


    }

    @Test
    void "WHEN add purchase and credit card is disabled, THEN should throws an exception"() {

        this.creditCard.setEnabled(Boolean.FALSE)
        def invoice = Invoice.createByCreditCard(this.creditCard)
        def transaction = new InvoiceTransaction(
                Category.CLOTHING,
                LocalDateTime.now(),
                "2 Blusas básicas - Cia Hering",
                new BigDecimal("-200")
        )

        assertThrows(BusinessException, { ->
            invoice.addPurchase(transaction)
        })


    }

    @Test
    void "WHEN add purchase and credit card is locked, THEN should throws an exception"() {

        this.creditCard.lock()
        def invoice = Invoice.createByCreditCard(this.creditCard)
        def transaction = new InvoiceTransaction(
                Category.CLOTHING,
                LocalDateTime.now(),
                "2 Blusas básicas - Cia Hering",
                new BigDecimal("-200")
        )

        assertThrows(BusinessException, { ->
            invoice.addPurchase(transaction)
        })


    }

    @Test
    void "WHEN add purchase and invoice is closed, THEN should throws an exception"() {

        def invoice = Invoice.createByCreditCard(this.creditCard)
        invoice.setClosingDate(LocalDate.now().minusDays(1))

        def transaction = new InvoiceTransaction(
                Category.CLOTHING,
                LocalDateTime.now(),
                "2 Blusas básicas - Cia Hering",
                new BigDecimal("-200")
        )

        assertThrows(BusinessException, { ->
            invoice.addPurchase(transaction)
        })

    }

    @Test
    void "WHEN add a payment THEN it should be in transactions and total should be the sum of transactions"() {

        def invoice = Invoice.createByCreditCard(this.creditCard)
        def value = new BigDecimal("100")

        def transaction1 = new InvoiceTransaction(
                Category.CLOTHING,
                LocalDateTime.now(),
                "2 Blusas básicas - Cia Hering",
                new BigDecimal("-200")
        )

        def transaction2 = new InvoiceTransaction(
                Category.RESTAURANT,
                LocalDateTime.now(),
                "Burguer King",
                new BigDecimal("-25")
        )

        def transaction3 = new InvoiceTransaction(
                Category.EDUCATION,
                LocalDateTime.now(),
                "Udemy",
                new BigDecimal("-25.60")
        )

        invoice.addPurchase(transaction1)
        invoice.addPurchase(transaction2)
        invoice.addPurchase(transaction3)

        invoice.addPayment(value)

        def total = transaction1.value + transaction2.value + transaction3.value + value

        assert invoice.transactions != null
        assert invoice.transactions.size() == 4
        assert invoice.transactions.containsAll(
                [transaction1, transaction2, transaction3]
        )

        assert total == invoice.total

    }

    @Test
    void "WHEN add a payment and value is greather than invoice's total, THEN should throws an exception"() {

        def invoice = Invoice.createByCreditCard(this.creditCard)
        def value = invoice.total + 1

        assertThrows(BusinessException, { ->
            invoice.addPayment(value)
        })

    }

    @Test
    void "WHEN add a payment and value is negative, THEN should throws an exception"() {

        def invoice = Invoice.createByCreditCard(this.creditCard)
        def value = new BigDecimal("-100")

        assertThrows(BusinessException, { ->
            invoice.addPayment(value)
        })

    }

    @Test
    void "WHEN add a payment and value is less than minimum payment, THEN should throws an exception"() {

        def invoice = Invoice.createByCreditCard(this.creditCard)

        def transaction1 = new InvoiceTransaction(
                Category.CLOTHING,
                LocalDateTime.now(),
                "2 Blusas básicas - Cia Hering",
                new BigDecimal("-200")
        )

        def transaction2 = new InvoiceTransaction(
                Category.RESTAURANT,
                LocalDateTime.now(),
                "Burguer King",
                new BigDecimal("-25")
        )

        def transaction3 = new InvoiceTransaction(
                Category.EDUCATION,
                LocalDateTime.now(),
                "Udemy",
                new BigDecimal("-25.60")
        )

        invoice.addPurchase(transaction1)
        invoice.addPurchase(transaction2)
        invoice.addPurchase(transaction3)

        def value = new BigDecimal("20.")

        assertThrows(BusinessException, { ->
            invoice.addPayment(value)
        })

    }

    @Test
    void "WHEN add a payment and credit card is disabled, THEN should throws an exception"() {

        def invoice = Invoice.createByCreditCard(this.creditCard)
        def value = new BigDecimal("100")

        def transaction1 = new InvoiceTransaction(
                Category.CLOTHING,
                LocalDateTime.now(),
                "2 Blusas básicas - Cia Hering",
                new BigDecimal("-200")
        )

        def transaction2 = new InvoiceTransaction(
                Category.RESTAURANT,
                LocalDateTime.now(),
                "Burguer King",
                new BigDecimal("-25")
        )

        def transaction3 = new InvoiceTransaction(
                Category.EDUCATION,
                LocalDateTime.now(),
                "Udemy",
                new BigDecimal("-25.60")
        )

        invoice.addPurchase(transaction1)
        invoice.addPurchase(transaction2)
        invoice.addPurchase(transaction3)

        this.creditCard.setEnabled(Boolean.FALSE)

        assertThrows(BusinessException, { ->
            invoice.addPayment(value)
        })

    }

    @Test
    void "WHEN add a payment and credit card is locked, THEN should throws an exception"() {

        def invoice = Invoice.createByCreditCard(this.creditCard)
        def value = new BigDecimal("100")

        def transaction1 = new InvoiceTransaction(
                Category.CLOTHING,
                LocalDateTime.now(),
                "2 Blusas básicas - Cia Hering",
                new BigDecimal("-200")
        )

        def transaction2 = new InvoiceTransaction(
                Category.RESTAURANT,
                LocalDateTime.now(),
                "Burguer King",
                new BigDecimal("-25")
        )

        def transaction3 = new InvoiceTransaction(
                Category.EDUCATION,
                LocalDateTime.now(),
                "Udemy",
                new BigDecimal("-25.60")
        )

        invoice.addPurchase(transaction1)
        invoice.addPurchase(transaction2)
        invoice.addPurchase(transaction3)

        this.creditCard.lock()

        assertThrows(BusinessException, { ->
            invoice.addPayment(value)
        })

    }

    @Test
    void "WHEN add a payment and invoice is closed, THEN should throws an exception"() {

        def invoice = Invoice.createByCreditCard(this.creditCard)

        def transaction1 = new InvoiceTransaction(
                Category.CLOTHING,
                LocalDateTime.now(),
                "2 Blusas básicas - Cia Hering",
                new BigDecimal("-200")
        )

        def transaction2 = new InvoiceTransaction(
                Category.RESTAURANT,
                LocalDateTime.now(),
                "Burguer King",
                new BigDecimal("-25")
        )

        def transaction3 = new InvoiceTransaction(
                Category.EDUCATION,
                LocalDateTime.now(),
                "Udemy",
                new BigDecimal("-25.60")
        )

        invoice.addPurchase(transaction1)
        invoice.addPurchase(transaction2)
        invoice.addPurchase(transaction3)

        def value = new BigDecimal("100")
        invoice.setClosingDate(LocalDate.now().minusMonths(2))

        assertThrows(BusinessException, { ->
            invoice.addPayment(value)
        })

    }

    void "WHEN two invoices have the same id, even if the other attributes are different, THEN they should be equal"() {

        Long id = 20L

        def a = Invoice.createByCreditCard(creditCard)
        a.setId(id)

        def b = Invoice.createByLastInvoice(a)
        b.setId(id)

        assert a == b

    }

}
