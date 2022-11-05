package org.devshred.quote.engine;

import static org.devshred.quote.KafkaTopics.TOPIC_PROCESS_ENGINE;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ProcessEngineApplication {
    public static void main(String... args) {
        SpringApplication.run(ProcessEngineApplication.class, args);
    }

    @Bean
    public NewTopic processEngine() {
        return new NewTopic(TOPIC_PROCESS_ENGINE, 1, (short) 1);
    }
}
