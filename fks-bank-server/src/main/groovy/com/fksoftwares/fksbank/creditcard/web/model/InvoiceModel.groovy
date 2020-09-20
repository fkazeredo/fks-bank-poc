package com.fksoftwares.fksbank.creditcard.web.model

import com.fksoftwares.fksbank.creditcard.InvoiceTransaction

import java.time.LocalDate

class InvoiceModel {
    Long id
    LocalDate dueDate
    BigDecimal total
    List<InvoiceTransaction> transactions
}
