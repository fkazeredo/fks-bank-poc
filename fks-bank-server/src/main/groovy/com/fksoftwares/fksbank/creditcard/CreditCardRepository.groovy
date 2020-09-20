package com.fksoftwares.fksbank.creditcard

import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.Repository
import org.springframework.data.repository.query.Param

interface CreditCardRepository extends Repository<CreditCard, Long> {

    CreditCard save(CreditCard creditCard)

    Optional<CreditCard> findById(Long id)
    Optional<CreditCard> findByUserProfileId(Long userProfileId)

    @Query("SELECT (e.currentLimit + SUM(it.value)) FROM #{#entityName} e INNER JOIN Invoice i ON i.creditCard.id = e.id INNER JOIN i.transactions it WHERE e.userProfileId = :userProfileId")
    BigDecimal findAvailableLimitByUserProfileId(@Param("userProfileId") Long userProfileId)

}
