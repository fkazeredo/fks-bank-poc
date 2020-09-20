package com.fksoftwares.fksbank.core.security


import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.stereotype.Component

@Component
class SecurityService {

    Long userId() {
        return jwt.isPresent() ? jwt.get().getClaim("id") as Long : 0L
    }

    Boolean isAuthenticated() {
        return authentication.isAuthenticated()
    }

    Boolean authenticatedUserEquals(Long anUserId) {
        Long userId = userId()
        return userId != null && userId == anUserId
    }

    Boolean hasAuthority(String aName) {
        return roles.stream().anyMatch({ authority -> authority.equalsIgnoreCase(aName) })
    }

    Boolean hasWriteScope() {
        return scopes.stream().anyMatch({ authority -> authority.equalsIgnoreCase("write") })
    }

    Boolean hasReadScope() {
        return scopes.stream().anyMatch({ authority -> authority.equalsIgnoreCase("read") })
    }

    String getUsername() {
        Optional<Jwt> maybeJwt = jwt
        return maybeJwt.isPresent() ? maybeJwt.get().getClaim("user_name") : "anonymousUser"
    }

    private Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication()
    }

    private Optional<Jwt> getJwt() {
        if (authentication != null && authentication.principal != "anonymousUser") {
            return Optional.of((Jwt) authentication.principal)
        }
        return Optional.empty()
    }

    private List<String> getRoles() {
        Optional<Jwt> maybeJwt = jwt
        return maybeJwt.isPresent() ? maybeJwt.get().getClaim("authorities") : [] as List<String>
    }

    private List<String> getScopes() {
        Optional<Jwt> maybeJwt = jwt
        return maybeJwt.isPresent() ? maybeJwt.get().getClaim("scope") : [] as List<String>
    }

}
