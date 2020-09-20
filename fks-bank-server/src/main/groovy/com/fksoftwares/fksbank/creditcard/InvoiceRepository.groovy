package com.fksoftwares.fksbank.creditcard

import com.fksoftwares.fksbank.creditcard.web.model.InvoiceSummaryModel
import org.springframework.cache.annotation.CacheEvict
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.Repository
import org.springframework.data.repository.query.Param

import java.time.LocalDate

interface InvoiceRepository extends Repository<Invoice, Long> {

    @CacheEvict(value = "invoiceSummary", allEntries = true)
    Invoice save(Invoice creditCard)

    @Query("SELECT new com.fksoftwares.fksbank.creditcard.web.model.InvoiceSummaryModel(MONTH(e.dueDate), YEAR(e.dueDate), SUM(it.value)) FROM #{#entityName} e INNER JOIN e.transactions it WHERE it.category != 'PAYMENT' AND e.creditCard.id = :creditCardId GROUP BY MONTH(e.dueDate), YEAR(e.dueDate)")
    List<InvoiceSummaryModel> findAllByCreditCardId(@Param("creditCardId") Long creditCardId)

    @Query("SELECT SUM(it.value) FROM #{#entityName} e INNER JOIN e.transactions it WHERE e.creditCard.id = :creditCardId AND CURRENT_DATE BETWEEN e.startDate AND e.closingDate")
    BigDecimal findCurrentTotalByCreditCardId(@Param("creditCardId") Long creditCardId)

    @Query("FROM #{#entityName} e WHERE e.id = :invoiceId AND e.creditCard.id = :creditCardId")
    Optional<Invoice> findByCreditCardIdAndInvoiceId(@Param("creditCardId") Long creditCardId, @Param("invoiceId") Long invoiceId)

    @Query("FROM #{#entityName} e INNER JOIN FETCH e.transactions WHERE e.creditCard.id = :creditCardId AND CURRENT_DATE BETWEEN e.startDate AND e.closingDate")
    Optional<Invoice> findCurrentByCreditCardId(@Param("creditCardId") Long creditCardId)

    @Query("FROM #{#entityName} e WHERE e.creditCard.id = :creditCardId AND e.startDate > :date")
    List<Invoice> findAllByCreditCardIdAfterDate(@Param("creditCardId") Long creditCardId, @Param("date") LocalDate date)

    @Query("FROM #{#entityName} e INNER JOIN FETCH e.transactions WHERE e.closingDate = :date")
    List<Invoice> findAllWithClosingDateOn(@Param("date") LocalDate date)

}
