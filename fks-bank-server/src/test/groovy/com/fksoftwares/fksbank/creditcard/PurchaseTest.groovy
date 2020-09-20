package com.fksoftwares.fksbank.creditcard

import com.fksoftwares.fksbank.core.BusinessException
import com.github.javafaker.Faker
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentCaptor

import java.time.LocalDateTime

import static org.junit.jupiter.api.Assertions.assertThrows
import static org.mockito.ArgumentMatchers.any
import static org.mockito.Mockito.*

class PurchaseTest {

    private InvoiceRepository invoiceRepository
    private CreditCardRepository creditCardRepository

    private Faker faker = new Faker()

    private CreditCard creditCard

    private Purchase purchase

    @BeforeEach
    void setUp() {
        this.creditCard = new CreditCard(1L, faker.name().fullName(), new BigDecimal("500."))
        this.creditCard.unlock()
        this.invoiceRepository = mock(InvoiceRepository)
        this.creditCardRepository = mock(CreditCardRepository)
        purchase = new Purchase(this.invoiceRepository, this.creditCardRepository)
        doReturn(Optional.of(this.creditCard))
                .when(this.creditCardRepository).findById(this.creditCard.id)
        doReturn(new BigDecimal("100000."))
                .when(this.creditCardRepository).findAvailableLimitByUserProfileId(any(Long))
    }

    @Test
    void "WHEN execute a purchase with 0 installments, THEN transaction should be in current invoice"() {

        def transaction = new InvoiceTransaction(
                Category.CLOTHING,
                LocalDateTime.now(),
                "2 Blusas básicas - Cia Hering",
                new BigDecimal("-200")
        )

        def invoiceCaptor = ArgumentCaptor.forClass(Invoice)

        def currentInvoice = Invoice.createByCreditCard(this.creditCard)
        doReturn(Optional.of(currentInvoice)).when(this.invoiceRepository).findCurrentByCreditCardId(this.creditCard.id)

        def nextMonthInvoice = Invoice.createByLastInvoice(currentInvoice)
        def twoMonthsInFutureInvoice = Invoice.createByLastInvoice(nextMonthInvoice)

        doReturn([nextMonthInvoice, twoMonthsInFutureInvoice] as List<Invoice>).when(invoiceRepository)
                .findAllByCreditCardIdAfterDate(this.creditCard.id, currentInvoice.closingDate)

        currentInvoice.setId(1L)
        doReturn(currentInvoice).when(this.invoiceRepository).save(any(Invoice))

        purchase.execute(creditCard.id, 0, Boolean.FALSE, transaction)

        verify(invoiceRepository, times(1)).save(invoiceCaptor.capture())
        def savedInvoice = invoiceCaptor.value

        assert savedInvoice != null
        assert savedInvoice.transactions.size() == 1

    }

    @Test
    void "WHEN execute a purchase with many installments, THEN transactions should be in current and next invoices and interest rates should be applied"() {

        def transaction = new InvoiceTransaction(
                Category.CLOTHING,
                LocalDateTime.now(),
                "2 Blusas básicas - Cia Hering",
                new BigDecimal("-1000")
        )

        def invoiceCaptor = ArgumentCaptor.forClass(Invoice)

        def currentInvoice = Invoice.createByCreditCard(this.creditCard)
        doReturn(Optional.of(currentInvoice)).when(this.invoiceRepository).findCurrentByCreditCardId(this.creditCard.id)

        def nextMonthInvoice = Invoice.createByLastInvoice(currentInvoice)
        def twoMonthsInFutureInvoice = Invoice.createByLastInvoice(nextMonthInvoice)

        doReturn([nextMonthInvoice, twoMonthsInFutureInvoice] as List<Invoice>).when(invoiceRepository)
                .findAllByCreditCardIdAfterDate(this.creditCard.id, currentInvoice.closingDate)

        currentInvoice.setId(1L)
        doReturn(currentInvoice).when(this.invoiceRepository).save(any(Invoice))

        purchase.execute(creditCard.id, 10, Boolean.TRUE, transaction)

        verify(invoiceRepository, times(10)).save(invoiceCaptor.capture())
        def allInvoices = invoiceCaptor.allValues

        assert allInvoices != null
        assert allInvoices.size() > 1
        allInvoices.each {
            def found = it.transactions.find {
                it.value = transaction.value
                it.description = transaction.description
                it.date = transaction.date
            }
            assert found != null
        }

    }

    @Test
    void "WHEN execute a purchase and transaction value is less than available credit card limit, THEN should throws an exception"() {

        def transaction = new InvoiceTransaction(
                Category.CLOTHING,
                LocalDateTime.now(),
                "2 Blusas básicas - Cia Hering",
                new BigDecimal("-200")
        )

        def currentInvoice = Invoice.createByCreditCard(this.creditCard)
        doReturn(Optional.of(currentInvoice)).when(this.invoiceRepository).findCurrentByCreditCardId(this.creditCard.id)

        def nextMonthInvoice = Invoice.createByLastInvoice(currentInvoice)
        def twoMonthsInFutureInvoice = Invoice.createByLastInvoice(nextMonthInvoice)

        doReturn([nextMonthInvoice, twoMonthsInFutureInvoice] as List<Invoice>).when(invoiceRepository)
                .findAllByCreditCardIdAfterDate(this.creditCard.id, currentInvoice.closingDate)

        currentInvoice.setId(1L)
        doReturn(currentInvoice).when(this.invoiceRepository).save(any(Invoice))

        doReturn(new BigDecimal("1."))
            .when(this.creditCardRepository).findAvailableLimitByUserProfileId(any(Long))

        assertThrows(BusinessException, { ->
            purchase.execute(creditCard.id, 0, Boolean.FALSE, transaction)
        })

    }


}
