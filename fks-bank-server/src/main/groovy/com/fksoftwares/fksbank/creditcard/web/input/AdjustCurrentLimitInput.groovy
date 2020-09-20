package com.fksoftwares.fksbank.creditcard.web.input

import javax.validation.constraints.NotNull

class AdjustCurrentLimitInput {
    @NotNull
    BigDecimal currentLimit
}
