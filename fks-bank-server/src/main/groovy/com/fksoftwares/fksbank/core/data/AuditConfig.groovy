package com.fksoftwares.fksbank.core.data

import com.fksoftwares.fksbank.core.security.SecurityService
import groovy.transform.PackageScope
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.domain.AuditorAware
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

@Configuration
@EnableJpaAuditing
@PackageScope
class AuditConfig {

    @Bean
    AuditorAware<String> auditorProvider(SecurityService securityService) {

        return new AuditorAware<String>() {

            @Override
            Optional<String> getCurrentAuditor() {
                return Optional.ofNullable(securityService.username)
            }
        }
    }

}
