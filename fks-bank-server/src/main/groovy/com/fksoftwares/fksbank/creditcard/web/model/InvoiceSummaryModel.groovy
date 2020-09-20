package com.fksoftwares.fksbank.creditcard.web.model

class InvoiceSummaryModel implements Serializable{

    Integer month
    Integer year
    BigDecimal total

    InvoiceSummaryModel(Integer month, Integer year, BigDecimal total) {
        this.month = month
        this.year = year
        this.total = total
    }

}
