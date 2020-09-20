package com.fksoftwares.fksbank.core.mail

import com.fksoftwares.fksbank.core.MailException
import freemarker.template.Configuration
import freemarker.template.Template
import freemarker.template.TemplateException
import groovy.transform.PackageScope
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.core.io.ClassPathResource
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils

import javax.mail.MessagingException
import javax.mail.internet.MimeMessage
import java.nio.charset.StandardCharsets

@Component
@PackageScope
class MailServiceImpl implements MailService {

    private Logger logger = LoggerFactory.getLogger(MailServiceImpl)

    private JavaMailSender emailSender
    private Configuration freemarkerConfig

    MailServiceImpl(JavaMailSender emailSender, Configuration freemarkerConfig) {
        this.emailSender = emailSender
        this.freemarkerConfig = freemarkerConfig
    }

    @Async
    void send(Mail mail) {

        try {

            MimeMessage message = emailSender.createMimeMessage()
            MimeMessageHelper helper = new MimeMessageHelper(message,
                    MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                    StandardCharsets.UTF_8.name())


            helper.addAttachment("logo.png", new ClassPathResource("logo.png"))

            Template template = freemarkerConfig.getTemplate("email-template.ftl")
            String html = FreeMarkerTemplateUtils.processTemplateIntoString(template, mail.model)

            helper.setTo(mail.getTo())
            helper.setText(html, true)
            helper.setSubject(mail.getSubject())
            helper.setFrom(mail.getFrom())

            emailSender.send(message)

        } catch (MessagingException | IOException | TemplateException e) {
            def to = mail.getTo()
            e.printStackTrace()
            logger.error("Erro ao enviar E-mail para {}, causa {}", to, e.getClass().getSimpleName())
            throw new MailException("mailException", to)
        }
    }

}
