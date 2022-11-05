package org.devshred.quote.rest;

import static org.devshred.quote.KafkaTopics.TOPIC_PROCESS_ENGINE;

import org.devshred.quote.model.ControlEvent;
import org.devshred.quote.model.StartQuoteProcess;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/start")
public class CamundaEndpoint {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @GetMapping
    public ResponseEntity simpleStart() {
        final ControlEvent startEvent = ControlEvent.startEvent();
        log.info("About to send {}.", startEvent);
        kafkaTemplate.send(TOPIC_PROCESS_ENGINE, startEvent);
        return ResponseEntity.noContent().build();
    }

    @PostMapping
    public ResponseEntity startWithParameters(@RequestBody final StartQuoteProcessDto dto) {
        final StartQuoteProcess event = dto.toEvent();
        log.info("About to send {}.", event);
        kafkaTemplate.send(TOPIC_PROCESS_ENGINE, event);
        return ResponseEntity.noContent().build();
    }
}
