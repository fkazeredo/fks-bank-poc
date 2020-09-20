package com.fksoftwares.fksbank.userprofile


import javax.persistence.Column
import javax.persistence.Embeddable
import java.time.LocalDateTime

@Embeddable
class PasswordRecoveryToken {

    private static final Integer EXPIRY_HOURS = 6

    @Column(name = "password_recovery_token_value")
    private String value

    @Column(name = "password_recovery_token_expiration_date")
    private LocalDateTime expirationDate

    @Column(name = "password_recovery_token_created_date")
    private LocalDateTime createdDate

    PasswordRecoveryToken(String value) {
        this.value = value
        this.createdDate = LocalDateTime.now()
        this.expirationDate = createdDate.plusHours(EXPIRY_HOURS)
    }

    Boolean isValid() {
        LocalDateTime.now().isBefore(this.expirationDate)
    }

    String getValue() {
        return value
    }

    LocalDateTime getExpirationDate() {
        return expirationDate
    }

    LocalDateTime getCreatedDate() {
        return createdDate
    }

    // JPA requirement
    protected PasswordRecoveryToken(){}

    // Unit tests requirement
    protected void setExpirationDate(LocalDateTime expirationDate) {
        this.expirationDate = expirationDate
    }
}
