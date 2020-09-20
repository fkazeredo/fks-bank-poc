package com.fksoftwares.fksbank.core.file

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.multipart.MultipartResolver
import org.springframework.web.multipart.commons.CommonsMultipartResolver

@Configuration
class FileUploadConfig {

    @Bean
    MultipartResolver multipartResolver(){
        def multipartResolver = new CommonsMultipartResolver()
        multipartResolver.setMaxUploadSize(5242880)
        return multipartResolver
    }

}
