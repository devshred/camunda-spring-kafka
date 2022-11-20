package org.devshred.quote;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class KafkaTopics {
    public static final String TOPIC_PROCESS_ENGINE = "process-engine";
    public static final String TOPIC_PROCESS_ENGINE_REPLY = "process-engine-reply";
    public static final String TOPIC_QUOTE_WORKER = "quote-worker";
}
