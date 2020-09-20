package com.fksoftwares.fksbank.core.mail


class Mail {

    String from
    String to
    String name
    String subject
    String content

    Map<String, String> model

    Mail(String from, String to, String subject, String content) {
        this.from = from
        this.to = to
        this.subject = subject
        this.content = content
    }

    Mail(){}

    Map<String, String> getModel(){
        return [
                from: this.from,
                to: this.to,
                name: this.name,
                subject: this.subject,
                content: this.content,
                signature: "Equipe FKS"
        ]
    }

    @Override
    String toString() {
        return "Mail{" +
                "from='" + from + '\'' +
                ", to='" + to + '\'' +
                ", subject='" + subject + '\'' +
                ", content='" + content + '\'' +
                '}'
    }

}
