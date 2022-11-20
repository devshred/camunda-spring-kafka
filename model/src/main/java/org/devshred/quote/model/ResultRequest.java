package org.devshred.quote.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResultRequest implements RequestEvent {
    String correlationId;
    String responseTopic;

    @Override
    public String responseTopic() {
        return responseTopic;
    }
}
