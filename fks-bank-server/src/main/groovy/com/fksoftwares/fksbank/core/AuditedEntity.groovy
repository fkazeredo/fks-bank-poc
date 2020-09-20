package com.fksoftwares.fksbank.core

import org.springframework.data.annotation.CreatedBy
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedBy
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener

import javax.persistence.EntityListeners
import javax.persistence.MappedSuperclass
import java.time.LocalDateTime

@MappedSuperclass
@EntityListeners(AuditingEntityListener)
abstract class AuditedEntity {

    @CreatedDate
    LocalDateTime createdDate

    @LastModifiedDate
    LocalDateTime modifiedDate

    @CreatedBy
    String createdBy

    @LastModifiedBy
    String modifiedBy

}
