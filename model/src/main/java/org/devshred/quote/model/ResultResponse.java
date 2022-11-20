package org.devshred.quote.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResultResponse implements ResponseEvent {
    String correlationId;
    String quote;
    Boolean finished;
}
