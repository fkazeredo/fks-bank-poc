package com.fksoftwares.fksbank.core.security.authorization

import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken
import org.springframework.security.oauth2.common.OAuth2AccessToken
import org.springframework.security.oauth2.provider.OAuth2Authentication
import org.springframework.security.oauth2.provider.token.TokenEnhancer

class JwtCustomClaimsTokenEnhancer implements TokenEnhancer {

    @Override
    OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {

        if (!authentication.isClientOnly()) {
            AuthUser authUser = authentication.getPrincipal() as AuthUser

            Map<String, Object> addInfo = new HashMap<>()
            addInfo.put("name", authUser.name)
            addInfo.put("id", authUser.id)
            ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(addInfo)
        }
        return accessToken
    }

}
