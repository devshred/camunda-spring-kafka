package org.devshred.quote.engine;

import static org.devshred.quote.KafkaTopics.TOPIC_PROCESS_ENGINE;

import java.util.List;
import java.util.Optional;

import org.camunda.bpm.engine.HistoryService;
import org.camunda.bpm.engine.history.HistoricProcessInstance;
import org.camunda.bpm.engine.history.HistoricVariableInstance;
import org.devshred.quote.model.ControlEvent;
import org.devshred.quote.model.ControlType;
import org.devshred.quote.model.Event;
import org.devshred.quote.model.QuoteProcessStart;
import org.devshred.quote.model.QuoteResponse;
import org.devshred.quote.model.RequestEvent;
import org.devshred.quote.model.ResponseEvent;
import org.devshred.quote.model.ResultRequest;
import org.devshred.quote.model.ResultResponse;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventReceiver {
    private final HistoryService historyService;
    private final ProcessService processService;

    @KafkaListener(id = "process-engine", topics = TOPIC_PROCESS_ENGINE)
    public void receiveEvents(Event event) {
        log.info("receiveEvent: {}", event);
        if (event instanceof RequestEvent || event instanceof ResponseEvent) {
            if (event instanceof final ControlEvent controlEvent) {
                if (controlEvent.getType() == ControlType.START_QUOTE) {
                    processService.startProcess(controlEvent.getCorrelationId(), "de_DE", "CHUCK_NORRIS");
                } else {
                    log.error("unknown type: {}", controlEvent.getType());
                }
            } else if (event instanceof final QuoteProcessStart quoteProcessStart) {
                processService.startProcess(quoteProcessStart.getCorrelationId(), quoteProcessStart.getLocale(),
                        quoteProcessStart.getType());
            } else if (event instanceof final QuoteResponse quoteResponse) {
                processService.processQuoteResponse(quoteResponse.getCorrelationId(), quoteResponse.getQuote());
            } else {
                log.error("unknown event: {}", event.getClass());
            }
        }
    }

    @KafkaListener(id = "process-engine-rest", topics = TOPIC_PROCESS_ENGINE)
    @SendTo
    public ResultResponse receiveReplyEvents(Event event) {
        log.info("receiveReplyEvent: {}", event);
        if (event instanceof final ResultRequest resultRequest) {
            final List<HistoricProcessInstance> instances = //
                    historyService.createHistoricProcessInstanceQuery() //
                            .finished() //
                            .processInstanceBusinessKeyIn(resultRequest.getCorrelationId()) //
                            .list();

            if (instances.isEmpty()) {
                log.warn("no result");
                return new ResultResponse(resultRequest.getCorrelationId(), null, false);
            } else if (instances.size() > 1) {
                log.warn("more than one result found");
                return new ResultResponse(resultRequest.getCorrelationId(), null, false);
            } else {
                final HistoricProcessInstance historicProcessInstance = instances.get(0);
                final Optional<HistoricVariableInstance> quote = //
                        historyService.createHistoricVariableInstanceQuery() //
                                .processInstanceId(historicProcessInstance.getId()) //
                                .list().stream().filter(v -> v.getName().equals("quote")) //
                                .findFirst();
                if (quote.isEmpty()) {
                    log.error("variable quote not found");
                    return new ResultResponse(resultRequest.getCorrelationId(), null, false);
                } else {
                    final String value = (String) quote.get().getValue();
                    return new ResultResponse(resultRequest.getCorrelationId(), value, true);
                }
            }
        } else {
            log.error("unknown event: {}", event.getClass());
        }
        return null;
    }
}
