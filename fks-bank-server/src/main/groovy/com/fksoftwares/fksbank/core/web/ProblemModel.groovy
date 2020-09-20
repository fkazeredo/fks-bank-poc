package com.fksoftwares.fksbank.core.web

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonInclude.Include

import java.time.LocalDateTime

@JsonInclude(Include.NON_NULL)
class ProblemModel {

    Integer status

    LocalDateTime timestamp

    String title

    List<Detail> details

    ProblemModel(Integer status, String title, List<Detail> details) {
        this.status = status
        this.timestamp = LocalDateTime.now()
        this.title = title
        this.details = details
    }

    static class Detail {

        String name

        String userMessage

        Detail(String name, String userMessage) {
            this.name = name
            this.userMessage = userMessage
        }
    }

}
