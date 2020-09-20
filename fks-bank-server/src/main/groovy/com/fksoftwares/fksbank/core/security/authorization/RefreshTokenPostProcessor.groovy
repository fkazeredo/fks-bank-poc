package com.fksoftwares.fksbank.core.security.authorization

import org.springframework.core.MethodParameter
import org.springframework.http.MediaType
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.http.server.ServerHttpRequest
import org.springframework.http.server.ServerHttpResponse
import org.springframework.http.server.ServletServerHttpRequest
import org.springframework.http.server.ServletServerHttpResponse
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken
import org.springframework.security.oauth2.common.OAuth2AccessToken
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice

import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@ControllerAdvice
class RefreshTokenPostProcessor implements ResponseBodyAdvice<OAuth2AccessToken> {

    @Override
    boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return returnType.getMethod().getName() == "postAccessToken"
    }

    @Override
    OAuth2AccessToken beforeBodyWrite(OAuth2AccessToken body, MethodParameter returnType,
                                             MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                             ServerHttpRequest request, ServerHttpResponse response) {

        def req =  (request as ServletServerHttpRequest).servletRequest
        def resp = (response as ServletServerHttpResponse).servletResponse

        if (body.getRefreshToken() != null) {
            DefaultOAuth2AccessToken token = (DefaultOAuth2AccessToken) body
            String refreshToken = body.refreshToken.value
            addRefreshTokenInCookie(refreshToken, req, resp)
            removeRefreshTokenFromBody(token)
        }

        return body
    }

    private void removeRefreshTokenFromBody(DefaultOAuth2AccessToken token) {
        token.refreshToken = null
    }

    private void addRefreshTokenInCookie(String refreshToken, HttpServletRequest req, HttpServletResponse resp) {
        Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken)
        refreshTokenCookie.httpOnly = Boolean.TRUE
        refreshTokenCookie.secure = Boolean.FALSE
        refreshTokenCookie.path = req.contextPath + "/oauth/token"
        refreshTokenCookie.maxAge = 36000
        resp.addCookie(refreshTokenCookie)
    }

}