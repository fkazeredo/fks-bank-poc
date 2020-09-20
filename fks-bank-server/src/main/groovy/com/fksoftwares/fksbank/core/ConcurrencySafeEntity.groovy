package com.fksoftwares.fksbank.core

import javax.persistence.MappedSuperclass
import javax.persistence.Version

@MappedSuperclass
abstract class ConcurrencySafeEntity extends AuditedEntity {

    @Version
    protected Integer version

}
