package com.longfish.collabai.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.longfish.collabai.constant.RabbitMQConstant.*;

@Configuration
public class RabbitMQConfig {

    @Bean
    public Queue emailQueue() {
        return new Queue(EMAIL_QUEUE, true);
    }

    @Bean
    public FanoutExchange emailExchange() {
        return new FanoutExchange(EMAIL_EXCHANGE, true, false);
    }

    @Bean
    public Binding bindingEmailDirect() {
        return BindingBuilder.bind(emailQueue()).to(emailExchange());
    }

    @Bean
    public Queue phoneQueue() {
        return new Queue(PHONE_QUEUE, true);
    }

    @Bean
    public FanoutExchange phoneExchange() {
        return new FanoutExchange(PHONE_EXCHANGE, true, false);
    }

    @Bean
    public Binding bindingPhoneDirect() {
        return BindingBuilder.bind(phoneQueue()).to(phoneExchange());
    }

    @Bean
    public Queue summaryQueue() {
        return new Queue(AI_SUMMARIZE_QUEUE, true);
    }

    @Bean
    public FanoutExchange summaryExchange() {
        return new FanoutExchange(AI_SUMMARIZE_EXCHANGE, true, false);
    }

    @Bean
    public Binding bindingSummaryDirect() {
        return BindingBuilder.bind(summaryQueue()).to(summaryExchange());
    }

}
