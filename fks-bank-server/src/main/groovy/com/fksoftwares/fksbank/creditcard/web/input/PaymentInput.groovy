package com.fksoftwares.fksbank.creditcard.web.input

import javax.validation.constraints.NotNull

class PaymentInput {
    @NotNull
    BigDecimal value
}
