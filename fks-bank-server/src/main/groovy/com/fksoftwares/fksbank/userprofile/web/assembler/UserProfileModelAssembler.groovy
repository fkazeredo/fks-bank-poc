package com.fksoftwares.fksbank.userprofile.web.assembler

import com.fksoftwares.fksbank.userprofile.UserProfile
import com.fksoftwares.fksbank.userprofile.web.model.UserProfileModel
import com.fksoftwares.fksbank.userprofile.web.model.UserProfileSummaryModel
import org.modelmapper.ModelMapper

class UserProfileModelAssembler {

    private static ModelMapper modelMapper = new ModelMapper()

    static UserProfileModel toModel(UserProfile userProfile) {
        return modelMapper.map(userProfile, UserProfileModel)
    }

    static UserProfileSummaryModel toSummary(UserProfile userProfile) {
        return modelMapper.map(userProfile, UserProfileSummaryModel)
    }

}
