package com.fksoftwares.fksbank.core.security

import groovy.transform.PackageScope
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter

import javax.crypto.spec.SecretKeySpec

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@PackageScope
class ResourceServerConfig extends WebSecurityConfigurerAdapter {


    private static final String[] AUTH_WHITELIST = [
            "/docs/**",
            "/webjars/**",
            "/public/**"
    ]

    @Override
    void configure(HttpSecurity http) throws Exception {
        http.sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .antMatchers(AUTH_WHITELIST).permitAll()
                .anyRequest().authenticated()
                .and()
                .csrf().disable()
                .oauth2ResourceServer().jwt()
                .jwtAuthenticationConverter(jwtAuthenticationConverter())
    }


    private JwtAuthenticationConverter jwtAuthenticationConverter() {
        def jwtAuthenticationConverter = new JwtAuthenticationConverter()
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter({ jwt ->
            def authorities = jwt.getClaimAsStringList("authorities")

            if (authorities == null) {
                authorities = Collections.emptyList()
            }

            def scopesAuthoritiesConverter = new JwtGrantedAuthoritiesConverter()
            Collection<GrantedAuthority> grantedAuthorities = scopesAuthoritiesConverter.convert(jwt)

            grantedAuthorities.addAll(authorities.collect{
                new SimpleGrantedAuthority(it)
            })

            return grantedAuthorities
        })

        return jwtAuthenticationConverter
    }

    @Bean
    @Override
    protected AuthenticationManager authenticationManager() throws Exception {
        return super.authenticationManager()
    }

    @Bean
    JwtDecoder jwtDecoder() {
        def secretKey = new SecretKeySpec("_A5VBA09O02k0t58zPw2kLth-_bAN32TDJ1h6Gs3j34".getBytes(), "HmacSHA256")

        return NimbusJwtDecoder.withSecretKey(secretKey).build()
    }

}