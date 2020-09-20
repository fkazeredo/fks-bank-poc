package com.fksoftwares.fksbank.userprofile.data

import com.fksoftwares.fksbank.core.data.BaseCriteriaRepository
import com.fksoftwares.fksbank.userprofile.UserProfile
import com.fksoftwares.fksbank.userprofile.UserProfileQueryRepository
import com.fksoftwares.fksbank.userprofile.web.filter.UserProfileSearchFilter
import com.fksoftwares.fksbank.userprofile.web.model.UserProfileSummaryModel
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import org.springframework.util.StringUtils

import javax.persistence.EntityManager
import javax.persistence.criteria.Predicate

@Repository
class UserProfileQueryRepositoryImpl extends BaseCriteriaRepository implements UserProfileQueryRepository {

    UserProfileQueryRepositoryImpl(EntityManager entityManager) {
        super(entityManager)
    }

    @Override
    Page<UserProfileSummaryModel> searchByFilter(UserProfileSearchFilter filter, Pageable pageable) {

        def builder = entityManager.criteriaBuilder
        def query = builder.createQuery(UserProfileSummaryModel)
        def root = query.from(UserProfile)

        query.select(
                builder.construct(
                        UserProfileSummaryModel,
                        root.get("id"),
                        root.get("cpf"),
                        root.get("name").get("firstName"),
                        root.get("name").get("lastName"),
                        root.get("username"),
                        root.get("enabled"),
                        root.get("status")))

        def predicates = new ArrayList<Predicate>()

        if (!StringUtils.isEmpty(filter.name))
            predicates.add(
                    builder.or(
                            builder.like(builder.lower(root.get("name").get("firstName")), "%" + filter.name.toLowerCase() + "%"),
                            builder.like(builder.lower(root.get("name").get("lastName")), "%" + filter.name.toLowerCase() + "%")
                    ))

        if (!StringUtils.isEmpty(filter.mail))
            predicates.add(builder.like(builder.lower(root.get("username")), "%" + filter.mail.toLowerCase() + "%"))

        if (filter.permission != null) {
            predicates.add(builder.equal(root.get("permission"), filter.permission))
        }

        if (filter.status != null) {
            predicates.add(builder.equal(root.get("status"), filter.status))
        }

        Boolean isEnabled = Boolean.TRUE
        if (filter.isEnabled != null)
            isEnabled = filter.isEnabled

        predicates.add(builder.equal(root.get("enabled"), isEnabled))

        return getPage(predicates, root, query, pageable)
    }

    @Override
    Boolean exists(UserProfile userProfile) {

        def builder = entityManager.getCriteriaBuilder()
        def query = builder.createQuery(Long)
        def root = query.from(UserProfile)

        query.select(
                root.get("id"))

        def predicates = new ArrayList<Predicate>()

        if (userProfile.getId() != null)
            predicates.add(builder.notEqual(root.get("id"), userProfile.getId()))

        predicates.add(
                builder.or(
                        builder.like(root.get("cpf"), userProfile.getCpf()),
                        builder.like(root.get("username"), userProfile.getUsername())
                )
        )

        query.where(predicates.toArray(new Predicate[0]))
        def typedQuery = entityManager.createQuery(query)

        return typedQuery.getResultList().size() > 0
    }
}
