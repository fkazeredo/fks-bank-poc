package com.fksoftwares.fksbank.core.queue.config

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.amqp.core.Binding
import org.springframework.amqp.core.BindingBuilder
import org.springframework.amqp.core.DirectExchange
import org.springframework.amqp.core.Queue
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter
import org.springframework.amqp.support.converter.MessageConverter
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Profile("dev")
@Configuration
class RabbitMQConfig {

    @Value("\${spring.rabbitmq.exchange}")
    String nitroBankTopicName

    @Value("\${spring.rabbitmq.queue.creditcard-purchase.name}")
    String creditCardPurchaseQueueName

    @Value("\${spring.rabbitmq.queue.userprofile-creation.name}")
    String userProfileCreationQueueName

    @Bean
    DirectExchange nitroBankTopic() {
        return new DirectExchange(nitroBankTopicName)
    }

    @Bean
    Queue creditCardPurchaseQueue() {
        return new Queue(creditCardPurchaseQueueName, true)
    }

    @Bean
    Queue userProfileCreationQueue() {
        return new Queue(userProfileCreationQueueName, true)
    }

    @Bean
    Binding financeBinding(Queue creditCardPurchaseQueue, DirectExchange nitroBankTopic) {
        return BindingBuilder.bind(creditCardPurchaseQueue).to(nitroBankTopic).with(creditCardPurchaseQueueName)
    }

    @Bean
    Binding marketingBinding(Queue userProfileCreationQueue, DirectExchange nitroBankTopic) {
        return BindingBuilder.bind(userProfileCreationQueue).to(nitroBankTopic).with(creditCardPurchaseQueueName)
    }

    MessageConverter jsonMessageConverter(ObjectMapper mapper) {
        return new Jackson2JsonMessageConverter(mapper)
    }

}
