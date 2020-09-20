package com.fksoftwares.fksbank.core.security.authorization

import com.fksoftwares.fksbank.core.EntityNotFoundException
import com.fksoftwares.fksbank.userprofile.UserProfile
import com.fksoftwares.fksbank.userprofile.UserProfileRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service

@Service
class AuthUserDetailsService implements UserDetailsService {

    private Logger logger = LoggerFactory.getLogger(AuthUserDetailsService)

    private UserProfileRepository userProfileRepository

    AuthUserDetailsService(UserProfileRepository userProfileRepository) {
        this.userProfileRepository = userProfileRepository
    }

    @Override
    UserDetails loadUserByUsername(String username) {

        Optional<UserProfile> maybeUserProfile = userProfileRepository.findByUsername(username)
        if (maybeUserProfile.isPresent()) {
            def userProfile = maybeUserProfile.get()
            return new AuthUser(
                    userProfile.id,
                    userProfile.name.fullName,
                    userProfile.username,
                    userProfile.password.value,
                    userProfile.isValid(),
                    getPermissoes(userProfile))
        } else {
            logger.error("Usuário {} não encontrado na base de dados", username)
            throw new EntityNotFoundException("userProfileNotFound", username)
        }

    }

    private Collection<? extends GrantedAuthority> getPermissoes(UserProfile userProfile) {

        Set<SimpleGrantedAuthority> authorities = new HashSet<>()
        authorities.add(new SimpleGrantedAuthority(userProfile.permission.name()))

        return authorities
    }

}
