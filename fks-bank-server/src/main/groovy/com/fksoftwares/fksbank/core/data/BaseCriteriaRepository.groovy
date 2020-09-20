package com.fksoftwares.fksbank.core.data

import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl

import javax.persistence.EntityManager
import javax.persistence.criteria.CriteriaBuilder
import javax.persistence.criteria.Predicate

abstract class BaseCriteriaRepository {

    EntityManager entityManager
    CriteriaBuilder builder

    BaseCriteriaRepository(EntityManager entityManager) {
        this.entityManager = entityManager
        this.builder = entityManager.getCriteriaBuilder()
    }

    List getList(predicates, query) {
        query.where(predicates.toArray(new Predicate[0]))
        def typedQuery = entityManager.createQuery(query)
        return typedQuery.getResultList()
    }

    Page getPage(predicates, root, query, pageable) {
        query.where(predicates.toArray(new Predicate[0]))
        def typedQuery = entityManager.createQuery(query)
        applyPagination(typedQuery, pageable)
        return new PageImpl<>(typedQuery.getResultList(), pageable, total(predicates, root))
    }

    void applyPagination(typedQuery, pageable) {
        typedQuery.setFirstResult(pageable.getPageNumber() * pageable.getPageSize())
        typedQuery.setMaxResults(pageable.getPageSize())
    }

    Long total(predicates, baseRoot) {
        def builder = entityManager.getCriteriaBuilder()
        def query = builder.createQuery(Long)
        def root = query.from(baseRoot.getModel().getBindableJavaType())

        for (def join : baseRoot.getJoins()) {
            root.join(join.getAttribute().getName())
        }

        query.select(builder.count(root))
        query.where(predicates.toArray(new Predicate[0]))

        return entityManager.createQuery(query).getSingleResult()
    }


}
