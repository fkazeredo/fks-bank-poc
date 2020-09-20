package com.fksoftwares.fksbank.core.security.authorization

import org.apache.catalina.util.ParameterMap
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component

import javax.servlet.*
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletRequestWrapper

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
class RefreshTokenCookiePreProcessorFilter implements Filter {

    @Override
    void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request

        if ("/oauth/token".equalsIgnoreCase(req.getRequestURI())
                && "refresh_token" == req.getParameter("grant_type")
                && req.getCookies() != null) {
            for (Cookie cookie : req.getCookies()) {
                if (cookie.getName() == "refreshToken") {
                    String refreshToken = cookie.getValue()
                    req = new MyServletRequestWrapper(req, refreshToken)
                }
            }
        }

        chain.doFilter(req, response)
    }

    @Override
    void destroy() {

    }

    @Override
    void init(FilterConfig arg0) throws ServletException {

    }

    static class MyServletRequestWrapper extends HttpServletRequestWrapper {

        private final String refreshToken

        MyServletRequestWrapper(HttpServletRequest request, String refreshToken) {
            super(request)
            this.refreshToken = refreshToken
        }

        @Override
        Map<String, String[]> getParameterMap() {
            ParameterMap<String, String[]> map = new ParameterMap<>(getRequest().getParameterMap())
            map.put("refresh_token", [refreshToken] as String[])
            map.setLocked(true)
            return map
        }

    }

}