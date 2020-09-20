package com.fksoftwares.fksbank.creditcard

import com.fksoftwares.fksbank.core.DomainEvent
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class UserProfileCreated implements DomainEvent, Serializable{

    private Logger logger = LoggerFactory.getLogger(UserProfileCreated)

    Long userProfileId
    String userProfileName
    BigDecimal maxLimit

    void handle(CreditCardRepository creditCardRepository) {

        def creditCard = creditCardRepository.save(new CreditCard(this.userProfileId, this.userProfileName, this.maxLimit))
        logger.info("Cartão de crédito de número ${creditCard.number} criado para o usuário ${creditCard.userProfileId}")

    }

}
