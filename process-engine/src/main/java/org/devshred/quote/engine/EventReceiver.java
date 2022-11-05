package org.devshred.quote.engine;

import static org.devshred.quote.KafkaTopics.TOPIC_PROCESS_ENGINE;

import org.camunda.bpm.engine.RuntimeService;
import org.devshred.quote.model.ControlEvent;
import org.devshred.quote.model.ControlType;
import org.devshred.quote.model.Event;
import org.devshred.quote.model.QuoteResponse;
import org.devshred.quote.model.RequestEvent;
import org.devshred.quote.model.ResponseEvent;
import org.devshred.quote.model.StartQuoteProcess;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventReceiver {
    private final RuntimeService runtimeService;
    private final ProcessService processService;

    @KafkaListener(id = "process-engine", topics = TOPIC_PROCESS_ENGINE)
    public void receiveEvents(Event event) {
        log.info("receiveEvent: {}", event);
        if (event instanceof RequestEvent || event instanceof ResponseEvent) {
            if (event instanceof final ControlEvent controlEvent) {
                if (controlEvent.getType() == ControlType.START_QUOTE) {
                    processService.startProcess("de_DE", "CHUCK_NORRIS");
                } else {
                    log.error("unknown type: {}", controlEvent.getType());
                }
            } else if (event instanceof final StartQuoteProcess startQuoteProcess) {
                processService.startProcess(startQuoteProcess.getLocale(), startQuoteProcess.getType());
            } else if (event instanceof final QuoteResponse quoteResponse) {
                processService.processQuoteResponse(quoteResponse.getCorrelationId(), quoteResponse.getQuote());
            } else {
                log.error("unknown event: {}", event.getClass());
            }
        }
    }
}
