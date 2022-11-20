package org.devshred.quote.rest;

import static org.devshred.quote.KafkaTopics.TOPIC_PROCESS_ENGINE;
import static org.devshred.quote.KafkaTopics.TOPIC_PROCESS_ENGINE_REPLY;

import java.util.concurrent.TimeUnit;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.devshred.quote.model.ControlEvent;
import org.devshred.quote.model.QuoteProcessStart;
import org.devshred.quote.model.ResultRequest;
import org.devshred.quote.model.ResultResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.requestreply.KafkaReplyTimeoutException;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.requestreply.RequestReplyFuture;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
@RestController
public class CamundaEndpoint {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ReplyingKafkaTemplate<String, ResultRequest, ResultResponse> replyingKafkaTemplate;

    @GetMapping("/start")
    public ResponseEntity simpleStart() {
        final ControlEvent startEvent = ControlEvent.startEvent();
        log.info("About to send {}.", startEvent);
        kafkaTemplate.send(TOPIC_PROCESS_ENGINE, startEvent);
        return ResponseEntity.accepted().body("started process with correlationId " + startEvent.getCorrelationId());
    }

    @PostMapping("/start")
    public ResponseEntity startWithParameters(@RequestBody final StartQuoteProcessDto dto) {
        final QuoteProcessStart event = dto.toEvent();
        log.info("About to send {}.", event);
        kafkaTemplate.send(TOPIC_PROCESS_ENGINE, event);
        return ResponseEntity.accepted().body("started process with correlationId " + event.getCorrelationId());
    }

    @GetMapping("/result/{correlationId}")
    public ResponseEntity result(@PathVariable("correlationId") final String correlationId) throws Exception {
        final ResultRequest event = new ResultRequest(correlationId, TOPIC_PROCESS_ENGINE_REPLY);
        final ProducerRecord<String, ResultRequest> record = new ProducerRecord<>(TOPIC_PROCESS_ENGINE, event);
        final RequestReplyFuture<String, ResultRequest, ResultResponse> replyFuture =
                replyingKafkaTemplate.sendAndReceive(record);
        try {
            final ConsumerRecord<String, ResultResponse> consumerRecord = replyFuture.get(10, TimeUnit.SECONDS);
            if (consumerRecord != null) {
                final ResultResponse resultResponse = consumerRecord.value();
                if (resultResponse.getFinished()) {
                    return ResponseEntity.ok(resultResponse);
                } else {
                    log.info("result {} not found or finished", correlationId);
                }
            }
        } catch (KafkaReplyTimeoutException e) {
            log.warn(e.getMessage());
        }
        return ResponseEntity.notFound().build();
    }
}
