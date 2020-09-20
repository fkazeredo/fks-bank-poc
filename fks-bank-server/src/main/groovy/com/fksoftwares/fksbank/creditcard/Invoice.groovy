package com.fksoftwares.fksbank.creditcard

import com.fksoftwares.fksbank.core.BusinessException
import com.fksoftwares.fksbank.core.ConcurrencySafeEntity

import javax.persistence.*
import javax.validation.constraints.NotNull
import java.math.RoundingMode
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime

@Entity
class Invoice extends ConcurrencySafeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id

    @NotNull
    private LocalDate startDate

    @NotNull
    private LocalDate closingDate

    @NotNull
    private LocalDate dueDate

    @ManyToOne
    @JoinColumn(name = "credit_card_id")
    private CreditCard creditCard

    @OneToOne
    @JoinColumn(name = "last_invoice_id")
    private Invoice lastInvoice

    @ElementCollection
    @CollectionTable(name = "invoice_transaction", joinColumns = @JoinColumn(name = "invoice_id"))
    private List<InvoiceTransaction> transactions = new ArrayList<>()

    static Invoice createByCreditCard(CreditCard creditCard) {
        def invoice = new Invoice()
        invoice.creditCard = creditCard
        invoice.dueDate = invoice.adjustDueDate(LocalDate.now().withDayOfMonth(creditCard.dueDay).plusMonths(1), invoice.creditCard.getDueDay())
        invoice.closingDate = invoice.adjustClosingDate()
        invoice.startDate = invoice.closingDate.minusMonths(1).withDayOfMonth(invoice.closingDate.dayOfMonth + 1)
        return invoice
    }

    static createByLastInvoice(Invoice lastInvoice) {
        def invoice = new Invoice()
        invoice.creditCard = lastInvoice.getCreditCard()
        invoice.dueDate = invoice.adjustDueDate(lastInvoice.getDueDate().plusMonths(1), invoice.creditCard.getDueDay())
        invoice.closingDate = invoice.adjustClosingDate()
        invoice.startDate = lastInvoice.getClosingDate().plusDays(1)
        invoice.lastInvoice = lastInvoice
        return invoice
    }

    void addPurchase(InvoiceTransaction transaction) {

        if (transaction.getValue() > new BigDecimal("0.")) {
            throw new BusinessException("invoicesPurchasePositive")
        }

        validateCreditCard()
        validateClosedInvoice()

        addTransaction(transaction)
    }

    void addPayment(BigDecimal value) {

        if (value > (total * -1)){
            throw new BusinessException("invoicesPaymentGreaterThanTotal")
        }

        if (value < new BigDecimal("0.")) {
            throw new BusinessException("invoicesPaymentNegative")
        }

        if (value < minimumPayment) {
            throw new BusinessException("invoiceMinimumPayment")
        }

        if (closed)
            throw new BusinessException("invoiceIsClosed")

        validateCreditCard()

        def transaction = new InvoiceTransaction(Category.PAYMENT, LocalDateTime.now(), Category.PAYMENT.getDescription(), value)
        addTransaction(transaction)
    }

    private void addTransaction(InvoiceTransaction transaction) {
        if (!this.transactions.contains(transaction))
            this.transactions.add(transaction)
    }

    private LocalDate adjustDueDate(LocalDate date, Integer day) {
        LocalDate newDate = date.withDayOfMonth(day)

        if (date.getDayOfWeek() == DayOfWeek.SATURDAY)
            newDate = date.plusDays(2)

        else if (date.getDayOfWeek() == DayOfWeek.SUNDAY)
            newDate = date.plusDays(1)

        return newDate
    }

    private LocalDate adjustClosingDate() {
        return this.dueDate.minusDays(7)
    }

    BigDecimal getTotal() {
        return this.transactions.stream().collect {
            it.value
        }.inject(this.initialTransactionValue) {
            lastValue,  value -> lastValue + value
        }
    }

    BigDecimal getMinimumPayment() {
        def positiveTotal = total > 0 ? total : total * -1
        return  (positiveTotal * 0.15 as BigDecimal).setScale(4, RoundingMode.HALF_EVEN)
    }

    BigDecimal getInitialTransactionValue(){
        return this.lastInvoice != null ? this.lastInvoice.total : new BigDecimal("0.")
    }

    Boolean isClosed() {
        return closingDate < LocalDate.now()
    }

    private void validateClosedInvoice() {
        if (this.isClosed())
            throw new BusinessException("invoiceClosed")
    }

    private void validateCreditCard() {
        this.creditCard.validateEnablement()
        this.creditCard.validateLocked()
    }

    Long getId() {
        return id
    }

    LocalDate getStartDate() {
        return startDate
    }

    LocalDate getClosingDate() {
        return closingDate
    }

    LocalDate getDueDate() {
        return dueDate
    }

    CreditCard getCreditCard() {
        return creditCard
    }

    Invoice getLastInvoice(){
        return lastInvoice
    }

    List<InvoiceTransaction> getTransactions() {
        return this.transactions.asImmutable()
    }

    boolean equals(o) {
        if (this.is(o)) return true
        if (!(o instanceof Invoice)) return false

        Invoice invoice = (Invoice) o

        if (id != invoice.id) return false

        return true
    }

    int hashCode() {
        return (id != null ? id.hashCode() : 0)
    }

    // JPA requirement
    protected Invoice() {}

    // Unit tests requirement
    protected setClosingDate(LocalDate closingDate){
        this.closingDate = closingDate
    }

    protected setId(Long id){
        this.id = id
    }

}
