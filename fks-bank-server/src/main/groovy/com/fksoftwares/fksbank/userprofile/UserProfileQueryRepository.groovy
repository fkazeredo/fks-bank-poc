package com.fksoftwares.fksbank.userprofile

import com.fksoftwares.fksbank.userprofile.web.filter.UserProfileSearchFilter
import com.fksoftwares.fksbank.userprofile.web.model.UserProfileSummaryModel
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface UserProfileQueryRepository {

    Page<UserProfileSummaryModel> searchByFilter(UserProfileSearchFilter filter, Pageable pageable)
    Boolean exists(UserProfile userProfile)

}
