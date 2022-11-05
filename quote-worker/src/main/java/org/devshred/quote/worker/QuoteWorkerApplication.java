package org.devshred.quote.worker;

import static org.devshred.quote.KafkaTopics.TOPIC_QUOTE_WORKER;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class QuoteWorkerApplication {
    public static void main(String[] args) {
        SpringApplication.run(QuoteWorkerApplication.class, args);
    }

    @Bean
    public NewTopic quoteWorker() {
        return new NewTopic(TOPIC_QUOTE_WORKER, 1, (short) 1);
    }
}
