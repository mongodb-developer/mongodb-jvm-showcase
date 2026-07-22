package com.devnexus.frauddetection.infrastructure.message.support;

import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.header.internals.RecordHeaders;

import java.nio.charset.StandardCharsets;
import java.time.Instant;

public final class KafkaHeaders {

    private KafkaHeaders() {}

    public static Headers forDlq(String sourceTopic, Exception exception) {
        return new Builder()
            .add("dlq.source.topic", sourceTopic)
            .add("dlq.error.message", exception.getMessage())
            .add("dlq.error.type", exception.getClass().getName())
            .add("dlq.timestamp", Instant.now().toString())
            .build();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final RecordHeaders headers = new RecordHeaders();

        public Builder add(String key, String value) {
            if (value != null) {
                headers.add(key, value.getBytes(StandardCharsets.UTF_8));
            }
            return this;
        }

        public Headers build() {
            return headers;
        }
    }
}