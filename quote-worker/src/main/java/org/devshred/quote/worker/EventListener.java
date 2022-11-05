package org.devshred.quote.worker;

import static org.devshred.quote.KafkaTopics.TOPIC_QUOTE_WORKER;

import java.util.Locale;

import org.devshred.quote.model.QuoteRequest;
import org.devshred.quote.model.QuoteResponse;
import org.devshred.quote.model.QuoteType;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.github.javafaker.Faker;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class EventListener {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @KafkaListener(id = "quotesGroup", topics = TOPIC_QUOTE_WORKER)
    public void listen(QuoteRequest request) {
        log.info("Received: " + request);

        final Faker faker = new Faker(new Locale(request.getLocale()));
        final String quote;
        switch (QuoteType.valueOf(request.getType())) {
            case BACK_TO_THE_FUTURE -> quote = faker.backToTheFuture().quote();
            case CHUCK_NORRIS -> quote = faker.chuckNorris().fact();
            case GAME_OF_THRONES -> quote = faker.gameOfThrones().quote();
            case HITCHHIKERS_GUIDE_TO_THE_GALAXY -> quote = faker.hitchhikersGuideToTheGalaxy().quote();
            case LEBOWSKI -> quote = faker.lebowski().quote();
            case RICK_AND_MORTY -> quote = faker.rickAndMorty().quote();
            case YODA -> quote = faker.yoda().quote();
            default -> {
                log.error("unknown type {}", request.getType());
                return;
            }
        }

        final QuoteResponse response = new QuoteResponse(request.getCorrelationId(), quote);
        kafkaTemplate.send(request.responseTopic(), response);
        log.info("Sent: {}", response);
    }
}
