package com.fksoftwares.fksbank.creditcard

import com.fksoftwares.fksbank.core.BusinessException
import com.github.javafaker.Faker
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import java.time.LocalDate

import static org.junit.jupiter.api.Assertions.assertThrows

class CreditCardTest {

    private Faker faker = new Faker()

    private Long userProfileId
    private String userProfileFullName
    private BigDecimal maxLimit

    @BeforeEach
    void setUp() {
        this.userProfileId = 1L
        this.userProfileFullName = faker.name().fullName()
        this.maxLimit = new BigDecimal("500.")
    }

    @Test
    void "WHEN create a credit card, THEN should assign all constructors parameters to attributes and should generate a number"() {

        def creditCard = new CreditCard(this.userProfileId, this.userProfileFullName, this.maxLimit)

        assert this.userProfileId == creditCard.userProfileId
        assert this.userProfileFullName == creditCard.userProfileFullName
        assert this.maxLimit == creditCard.maxLimit
        assert creditCard.number != null

    }

    @Test
    void "WHEN create a credit card, THEN should set 'member since' to now and 'valid thru' to now plus 5 years"() {

        def creditCard = new CreditCard(this.userProfileId, this.userProfileFullName, this.maxLimit)
        def now = LocalDate.now()

        assert creditCard.memberSince == now
        assert creditCard.validThru == now.plusYears(5)

    }

    @Test
    void "WHEN create a credit card, THEN should set 'due day' to 5"() {

        def creditCard = new CreditCard(this.userProfileId, this.userProfileFullName, this.maxLimit)

        assert creditCard.dueDay == 5

    }

    @Test
    void "WHEN create a credit card, THEN it should be locked and enabled"() {

        def creditCard = new CreditCard(this.userProfileId, this.userProfileFullName, this.maxLimit)

        assert creditCard.locked == Boolean.TRUE
        assert creditCard.enabled == Boolean.TRUE

    }

    @Test
    void "WHEN creating a credit card, THEN should set the current limit to half the max limit"() {

        def creditCard = new CreditCard(this.userProfileId, this.userProfileFullName, this.maxLimit)

        assert creditCard.maxLimit / 2 == creditCard.currentLimit

    }

    @Test
    void "WHEN adjust limit and credit card is unlocked, THEN should change the current limit value"() {

        def newlimit = new BigDecimal("100.")

        def creditCard = new CreditCard(this.userProfileId, this.userProfileFullName, this.maxLimit)
        creditCard.unlock()
        creditCard.adjustLimit(newlimit)

        assert newlimit == creditCard.currentLimit

    }

    @Test
    void "WHEN adjust limit and credit card is disabled, THEN should throws an exception"() {

        def newlimit = new BigDecimal("100.")

        def creditCard = new CreditCard(this.userProfileId, this.userProfileFullName, this.maxLimit)
        creditCard.unlock()
        creditCard.setEnabled(Boolean.FALSE)

        assertThrows(BusinessException, { ->

            creditCard.adjustLimit(newlimit)

        })

    }

    @Test
    void "WHEN adjust limit and credit card is locked, THEN should throws an exception"() {

        def newlimit = new BigDecimal("100.")

        def creditCard = new CreditCard(this.userProfileId, this.userProfileFullName, this.maxLimit)

        assertThrows(BusinessException, { ->

            creditCard.adjustLimit(newlimit)

        })

    }

    @Test
    void "WHEN adjust limit and credit card is expirated, THEN should throws an exception"() {

        def newlimit = new BigDecimal("100.")

        def creditCard = new CreditCard(this.userProfileId, this.userProfileFullName, this.maxLimit)
        creditCard.unlock()
        creditCard.setValidThru(LocalDate.now().minusDays(1))

        assertThrows(BusinessException, { ->

            creditCard.adjustLimit(newlimit)

        })

    }

    @Test
    void "WHEN adjust limit and new limit value is less than zero, THEN should throws an exception"() {

        def newlimit = new BigDecimal("-1.")

        def creditCard = new CreditCard(this.userProfileId, this.userProfileFullName, this.maxLimit)
        creditCard.unlock()

        assertThrows(BusinessException, { ->

            creditCard.adjustLimit(newlimit)

        })

    }

    @Test
    void "WHEN adjust limit and new limit value is equal zero, THEN should throws an exception"() {

        def newlimit = new BigDecimal("0.")

        def creditCard = new CreditCard(this.userProfileId, this.userProfileFullName, this.maxLimit)
        creditCard.unlock()

        assertThrows(BusinessException, { ->

            creditCard.adjustLimit(newlimit)

        })

    }

    @Test
    void "WHEN adjust limit and new limit value is grater than max limit, THEN should throws an exception"() {


        def creditCard = new CreditCard(this.userProfileId, this.userProfileFullName, this.maxLimit)
        creditCard.unlock()
        def newlimit = creditCard.maxLimit + 1

        creditCard.unlock()

        assertThrows(BusinessException, { ->

            creditCard.adjustLimit(newlimit)

        })

    }

    @Test
    void "WHEN change due day and credit card is unlocked, THEN should change the due day value"() {

        def newDueDay = 15

        def creditCard = new CreditCard(this.userProfileId, this.userProfileFullName, this.maxLimit)
        creditCard.unlock()
        creditCard.changeDueDay(newDueDay)

        assert newDueDay == creditCard.dueDay

    }

    @Test
    void "WHEN change due day and credit card is disabled, THEN should throws an exception"() {

        def newDueDay = 15

        def creditCard = new CreditCard(this.userProfileId, this.userProfileFullName, this.maxLimit)
        creditCard.unlock()
        creditCard.setEnabled(Boolean.FALSE)

        assertThrows(BusinessException, { ->

            creditCard.changeDueDay(newDueDay)

        })

    }

    @Test
    void "WHEN change due day and credit card is locked, THEN should throws an exception"() {

        def newDueDay = 15

        def creditCard = new CreditCard(this.userProfileId, this.userProfileFullName, this.maxLimit)

        assertThrows(BusinessException, { ->

            creditCard.changeDueDay(newDueDay)

        })

    }

    @Test
    void "WHEN change due day and credit card is expirated, THEN should throws an exception"() {

        def newDueDay = 15

        def creditCard = new CreditCard(this.userProfileId, this.userProfileFullName, this.maxLimit)
        creditCard.unlock()
        creditCard.setValidThru(LocalDate.now().minusDays(1))

        assertThrows(BusinessException, { ->

            creditCard.changeDueDay(newDueDay)

        })

    }

    @Test
    void "WHEN unlock and credit card is enabled, THEN should change locked value to false"() {

        def creditCard = new CreditCard(this.userProfileId, this.userProfileFullName, this.maxLimit)
        creditCard.unlock()

        assert !creditCard.locked

    }

    @Test
    void "WHEN unlock and credit card is disabled, THEN should throws an exception"() {

        def creditCard = new CreditCard(this.userProfileId, this.userProfileFullName, this.maxLimit)
        creditCard.setEnabled(Boolean.FALSE)

        assertThrows(BusinessException, { ->

            creditCard.unlock()

        })

    }

    @Test
    void "WHEN lock and credit card is enabled, THEN should change locked value to true"() {

        def creditCard = new CreditCard(this.userProfileId, this.userProfileFullName, this.maxLimit)
        creditCard.unlock()

        assert !creditCard.locked

        creditCard.lock()

        assert creditCard.locked

    }

    @Test
    void "WHEN lock and credit card is disabled, THEN should throws an exception"() {

        def creditCard = new CreditCard(this.userProfileId, this.userProfileFullName, this.maxLimit)
        creditCard.unlock()
        creditCard.setEnabled(Boolean.FALSE)

        assertThrows(BusinessException, { ->

            creditCard.unlock()

        })

    }

    void "WHEN two credit cards have the same id and the same user profile id, even if the other attributes are different, THEN they should be equal"() {

        Long id = 20L

        def a = new CreditCard(this.userProfileId, this.userProfileFullName, this.maxLimit)
        a.setId(id)

        def b = new CreditCard(this.userProfileId, "Lana Raquel", new BigDecimal("900"))
        b.setId(id)

        assert a == b

    }

}
