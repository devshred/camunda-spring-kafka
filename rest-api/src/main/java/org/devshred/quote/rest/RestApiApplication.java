package org.devshred.quote.rest;


import static org.devshred.quote.KafkaTopics.TOPIC_PROCESS_ENGINE_REPLY;

import java.time.Duration;

import org.devshred.quote.model.ResultRequest;
import org.devshred.quote.model.ResultResponse;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;

@SpringBootApplication
public class RestApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(RestApiApplication.class, args);
    }

    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate(ProducerFactory<String, Object> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }

    @Bean
    public ReplyingKafkaTemplate<String, ResultRequest, ResultResponse> replyingTemplate(
            ProducerFactory<String, ResultRequest> pf,
            ConcurrentMessageListenerContainer<String, ResultResponse> repliesContainer) {
        ReplyingKafkaTemplate<String, ResultRequest, ResultResponse> replyTemplate =
                new ReplyingKafkaTemplate<>(pf, repliesContainer);
        replyTemplate.setDefaultReplyTimeout(Duration.ofSeconds(10));
        replyTemplate.setSharedReplyTopic(true);
        return replyTemplate;
    }

    @Bean
    public ConcurrentMessageListenerContainer<String, ResultResponse> repliesContainer(
            ConcurrentKafkaListenerContainerFactory<String, ResultResponse> containerFactory) {
        ConcurrentMessageListenerContainer<String, ResultResponse> repliesContainer =
                containerFactory.createContainer(TOPIC_PROCESS_ENGINE_REPLY);
        repliesContainer.getContainerProperties().setGroupId("rest-api-consumer");
        repliesContainer.setAutoStartup(false);
        return repliesContainer;
    }
}
