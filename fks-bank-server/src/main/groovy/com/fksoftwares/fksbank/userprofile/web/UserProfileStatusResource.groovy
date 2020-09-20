package com.fksoftwares.fksbank.userprofile.web

import com.fksoftwares.fksbank.userprofile.Permission
import com.fksoftwares.fksbank.userprofile.UserProfileStatus
import groovy.transform.PackageScope
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/user-profile-status")
@PackageScope
class UserProfileStatusResource {

    @GetMapping
    @PreAuthorize("@securityService.hasAuthority('MANAGER') and @securityService.hasReadScope()")
    List<Map<String, String>> findAll() {
        return UserProfileStatus.values()
                .findAll { it != UserProfileStatus.PENDING }
                .collect {
                    [name: it.name(), description: it.description]
                } as List<Map<String, String>>
    }

}
