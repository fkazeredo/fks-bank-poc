package com.fksoftwares.fksbank.creditcard

import com.fksoftwares.fksbank.core.BusinessException
import com.fksoftwares.fksbank.core.ConcurrencySafeEntity
import com.github.javafaker.CreditCardType
import com.github.javafaker.Faker
import org.hibernate.validator.constraints.CreditCardNumber

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size
import java.math.RoundingMode
import java.time.LocalDate

@Entity
class CreditCard extends ConcurrencySafeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id

    @NotNull
    private Long userProfileId

    @NotBlank
    private String userProfileFullName

    @NotBlank
    @CreditCardNumber
    @Size(min = 16, max = 16)
    private String number

    @NotNull
    private LocalDate memberSince

    @NotNull
    private LocalDate validThru

    @NotNull
    private BigDecimal maxLimit

    @NotNull
    private BigDecimal currentLimit

    @NotNull
    private Integer dueDay

    @NotNull
    private Boolean locked

    @NotNull
    private Boolean enabled

    CreditCard(Long userProfileId, String userProfileFullName, BigDecimal maxLimit) {
        this.userProfileId = userProfileId
        this.userProfileFullName = userProfileFullName
        this.maxLimit = maxLimit
        this.number = new Faker().finance().creditCard(CreditCardType.MASTERCARD).replace("-", "")
        this.memberSince = LocalDate.now()
        this.validThru = this.memberSince.plusYears(5)
        this.dueDay = 5
        this.locked = Boolean.TRUE
        this.enabled = Boolean.TRUE
        this.setInitialCurrentLimit()
    }

    void adjustLimit(BigDecimal limitValue) {

        validate()

        if (limitValue <= new BigDecimal("0."))
            throw new BusinessException("creditCardZeroLimit")

        if (limitValue > this.maxLimit)
            throw new BusinessException("creditCardLimitGreaterThanMaxLimit")

        this.currentLimit = limitValue
    }

    void changeDueDay(Integer day) {

        validate()

        this.dueDay = day
    }

    void unlock() {

        validateEnablement()

        if (this.locked)
            this.locked = Boolean.FALSE
    }

    void lock() {

        validateEnablement()

        if (!this.locked)
            this.locked = Boolean.TRUE
    }

    protected void validateLocked() {
        if (this.locked)
            throw new BusinessException("creditCardLocked")
    }

    protected void validateEnablement() {
        if (!this.enabled)
            throw new BusinessException("creditCardDisabled")
    }

    private void validateExpiration() {
        if (this.validThru.isBefore(LocalDate.now())) {
            throw new BusinessException("creditCardExpirated")
        }
    }

    private void validate() {
        validateEnablement()
        validateLocked()
        validateExpiration()
    }

    private void setInitialCurrentLimit() {
        this.currentLimit = (maxLimit / 2 as BigDecimal).setScale(4, RoundingMode.HALF_EVEN)
    }

    Long getId() {
        return id
    }

    Long getUserProfileId() {
        return userProfileId
    }

    String getUserProfileFullName() {
        return userProfileFullName
    }

    String getNumber() {
        return number
    }

    LocalDate getMemberSince() {
        return memberSince
    }

    LocalDate getValidThru() {
        return validThru
    }

    BigDecimal getMaxLimit() {
        return maxLimit
    }

    BigDecimal getCurrentLimit() {
        return currentLimit
    }

    Integer getDueDay() {
        return dueDay
    }

    Boolean getLocked() {
        return locked
    }

    Boolean getEnabled() {
        return enabled
    }

    String getLastNumbers(){
        Integer max = number.length()
        Integer min = max - 4
        return number.substring(min, max)
    }

    boolean equals(o) {
        if (this.is(o)) return true
        if (!(o instanceof CreditCard)) return false

        CreditCard that = (CreditCard) o

        if (id != that.id) return false
        if (userProfileId != that.userProfileId) return false

        return true
    }

    int hashCode() {
        int result
        result = (id != null ? id.hashCode() : 0)
        result = 31 * result + (userProfileId != null ? userProfileId.hashCode() : 0)
        return result
    }

    // JPA requirement
    protected CreditCard() {
    }

    // Unit tests requirement
    protected void setId(Long id){
        this.id = id
    }

    protected void setEnabled(Boolean enabled){
        this.enabled = enabled
    }

    protected void setValidThru(LocalDate validThru){
        this.validThru = validThru
    }

}
