package org.devshred.quote.engine;

import static org.devshred.quote.KafkaTopics.TOPIC_PROCESS_ENGINE;
import static org.devshred.quote.KafkaTopics.TOPIC_QUOTE_WORKER;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.devshred.quote.model.QuoteRequest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class RequestQuoteAdapter implements JavaDelegate {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public void execute(DelegateExecution delegateExecution) {
        final String businessKey = delegateExecution.getProcessBusinessKey();
        final String locale = (String) delegateExecution.getVariable(ProcessConstants.VAR_NAME_locale);
        final String quoteType = (String) delegateExecution.getVariable(ProcessConstants.VAR_NAME_quoteType);
        log.info("businessKey: {}", businessKey);
        log.info("locale: {}", locale);
        log.info("quoteType: {}", quoteType);

        final QuoteRequest request =
                new QuoteRequest(delegateExecution.getProcessBusinessKey(), quoteType, locale, TOPIC_PROCESS_ENGINE);
        log.info("about to send {}", request);
        kafkaTemplate.send(TOPIC_QUOTE_WORKER, request);
    }
}
