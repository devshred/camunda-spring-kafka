package org.devshred.quote.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ControlEvent implements RequestEvent {
    private String correlationId;
    private ControlType type;

    public static ControlEvent startEvent() {
        return new ControlEvent(UUID.randomUUID().toString(), ControlType.START_QUOTE);
    }

    @Override
    public String responseTopic() {
        return null;
    }
}
