package com.fksoftwares.fksbank.userprofile

import com.fksoftwares.fksbank.core.mail.Mail
import com.fksoftwares.fksbank.core.mail.MailService
import freemarker.template.Configuration
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.validation.annotation.Validated

import javax.validation.Valid

@Service
@Validated
class UserProfileMailer {

    private Logger logger = LoggerFactory.getLogger(UserProfileMailer)

    private MailService mailService
    private Configuration freemarkerConfig

    UserProfileMailer(MailService mailService, Configuration freemarkerConfig) {
        this.mailService = mailService
        this.freemarkerConfig = freemarkerConfig
    }

    void sendGreetings(@Valid UserProfile userProfile) {

        Mail mail = new Mail()
        mail.setFrom("fkazeredo@gmail.com")
        mail.setTo(userProfile.getUsername())
        mail.setName(userProfile.getName().getFullName())
        mail.setSubject("Bem-vindo ao FKS Bank, seu perfil será analisado por nossos especialistas")
        mail.setContent("Por favor aguarde!")

        mailService.send(mail)

        logger.info("Greetings mail sent to {}", userProfile.getUsername())

    }

    void sendApprovalMessage(UserProfile userProfile) {

        String passwordRecoveryUrl = generatePasswordRecoveryTokenUrl(userProfile)

        Mail mail = new Mail()
        mail.setFrom("fkazeredo@gmail.com")
        mail.setTo(userProfile.getUsername())
        mail.setName(userProfile.getName().getFullName())
        mail.setSubject("Um novo banco entrou em sua vida!")
        mail.setContent("""
                Cadastre uma senha no endereço: ${passwordRecoveryUrl}
                e logo depois você já pode acessar o aplicativo com o seu e-mail e CPF
                """)

        mailService.send(mail)

        logger.info("Approval mail sent to {}", userProfile.getUsername())

    }

    void sendRejectionMessage(UserProfile userProfile) {

        Mail mail = new Mail()
        mail.setFrom("fkazeredo@gmail.com")
        mail.setTo(userProfile.getUsername())
        mail.setName(userProfile.getName().getFullName())
        mail.setSubject("Infelizmente você não foi aprovado para o nosso banco")
        mail.setContent("Tente novamente daqui a um tempo")

        mailService.send(mail)

        logger.info("Rejection mail sent to {}", userProfile.getUsername())

    }

    void sendPasswordRecoveryLink(UserProfile userProfile) {

        String passwordRecoveryUrl = generatePasswordRecoveryTokenUrl(userProfile)

        Mail mail = new Mail(
                from: "fkazeredo@gmail.com",
                to: userProfile.getUsername(),
                name: userProfile.getName().getFullName(),
                subject: "Link de recuperação de senha!",
                content: passwordRecoveryUrl
        )

        mailService.send(mail)

        logger.info("Password Recovery mail sent to {}", userProfile.getUsername())

    }

    private String generatePasswordRecoveryTokenUrl(UserProfile userProfile) {
        return "http://localhost:4200/home/password-recovery/" + userProfile.getPasswordRecoveryToken().getValue()
    }
}
