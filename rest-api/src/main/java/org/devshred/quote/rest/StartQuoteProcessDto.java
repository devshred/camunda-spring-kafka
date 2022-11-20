package org.devshred.quote.rest;

import static org.devshred.quote.KafkaTopics.TOPIC_PROCESS_ENGINE;

import java.util.UUID;

import org.devshred.quote.model.QuoteProcessStart;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StartQuoteProcessDto {
    String type;
    String locale;

    public QuoteProcessStart toEvent() {
        return new QuoteProcessStart(UUID.randomUUID().toString(), type, locale, TOPIC_PROCESS_ENGINE);
    }
}
