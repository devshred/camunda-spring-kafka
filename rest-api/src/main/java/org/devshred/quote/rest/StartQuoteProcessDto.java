package org.devshred.quote.rest;

import static org.devshred.quote.KafkaTopics.TOPIC_PROCESS_ENGINE;

import org.devshred.quote.model.StartQuoteProcess;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StartQuoteProcessDto {
    String type;
    String locale;

    public StartQuoteProcess toEvent() {
        return new StartQuoteProcess(type, locale, TOPIC_PROCESS_ENGINE);
    }
}
