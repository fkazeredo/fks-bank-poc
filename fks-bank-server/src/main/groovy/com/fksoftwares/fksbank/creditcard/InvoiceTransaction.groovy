package com.fksoftwares.fksbank.creditcard

import javax.persistence.Embeddable
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size
import java.time.LocalDateTime

@Embeddable
class InvoiceTransaction {

    @NotNull
    @Enumerated(EnumType.STRING)
    private Category category

    @NotNull
    private LocalDateTime date

    @NotBlank
    @Size(min = 1, max = 150)
    private String description

    @NotNull
    private BigDecimal value

    InvoiceTransaction(Category category, LocalDateTime date, String description, BigDecimal value) {
        this.category = category
        this.date = date
        this.description = description
        this.value = value
    }

    Category getCategory() {
        return category
    }

    LocalDateTime getDate() {
        return date
    }

    String getDescription() {
        return description
    }

    BigDecimal getValue() {
        return value
    }

    // JPA requirement
    protected InvoiceTransaction() {
    }

}
