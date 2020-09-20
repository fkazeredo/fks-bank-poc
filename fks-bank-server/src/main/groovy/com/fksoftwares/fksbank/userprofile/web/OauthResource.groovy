package com.fksoftwares.fksbank.userprofile.web


import groovy.transform.PackageScope
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@RestController
@RequestMapping("/auth/token")
@PackageScope
class OauthResource {

    @DeleteMapping("/revoke")
    @PreAuthorize("@securityService.isAuthenticated()")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void revokeToken(HttpServletRequest req, HttpServletResponse resp) {

        Cookie cookie = new Cookie("refreshToken", null)
        cookie.httpOnly = Boolean.TRUE
        cookie.secure = Boolean.FALSE
        cookie.path = req.contextPath + "/oauth/token"
        cookie.maxAge = 0

        resp.addCookie(cookie)
    }

}
