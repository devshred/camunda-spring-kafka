package org.devshred.quote.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuoteRequest implements RequestEvent {
    String correlationId;
    String type;
    String locale;
    String responseTopic;

    @Override
    public String responseTopic() {
        return responseTopic;
    }
}
