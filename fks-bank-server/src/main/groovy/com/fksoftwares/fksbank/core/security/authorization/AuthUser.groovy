package com.fksoftwares.fksbank.core.security.authorization


import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.User

class AuthUser extends User {

    private static final long serialVersionUID = 1L

    String name
    String username
    Boolean enabled
    Long id

    AuthUser(Long id, String name, String username, String password, Boolean enabled, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities)
        this.id = id
        this.name = name
        this.username = username
        this.enabled = enabled
    }

    @Override
    boolean isEnabled() {
        return getEnabled()
    }

}
