package com.fksoftwares.fksbank.creditcard.web.input

import javax.validation.constraints.NotNull

class ChangeLockedInput {
    @NotNull
    Boolean locked
}
