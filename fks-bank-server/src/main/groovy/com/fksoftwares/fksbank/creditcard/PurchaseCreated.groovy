package com.fksoftwares.fksbank.creditcard

import com.fksoftwares.fksbank.core.DomainEvent
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.time.LocalDateTime

class PurchaseCreated implements DomainEvent, Serializable {

    private Logger logger = LoggerFactory.getLogger(PurchaseCreated)

    Long creditCardId
    Integer installments
    Boolean hasInstallmentsInterest
    Category category
    LocalDateTime date
    String description
    BigDecimal value

    void handle(Purchase purchase) {

        logger.info("Iniciando pagamento para o cartão {}", this.creditCardId)

        try {

            purchase.execute(
                    this.creditCardId,
                    this.installments,
                    this.hasInstallmentsInterest,
                    new InvoiceTransaction(
                            this.category,
                            this.date,
                            this.description,
                            this.value
                    )
            )

        } catch (Exception e) {
            logger.info("Falha ao executar pagamento para o cartão {}", this.creditCardId)
            e.printStackTrace()
        }

    }

}
