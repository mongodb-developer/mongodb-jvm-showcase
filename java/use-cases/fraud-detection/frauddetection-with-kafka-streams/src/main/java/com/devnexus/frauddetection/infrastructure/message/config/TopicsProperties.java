package com.devnexus.frauddetection.infrastructure.message.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.kafka.topics")
public record TopicsProperties(
    String transactions,
    String suspicious,
    String toScore,
    String dlq
) {}
