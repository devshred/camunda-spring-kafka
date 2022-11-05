package org.devshred.quote.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ControlEvent implements RequestEvent {
    private ControlType type;

    public static ControlEvent startEvent() {
        return new ControlEvent(ControlType.START_QUOTE);
    }

    @Override
    public String responseTopic() {
        return null;
    }
}
