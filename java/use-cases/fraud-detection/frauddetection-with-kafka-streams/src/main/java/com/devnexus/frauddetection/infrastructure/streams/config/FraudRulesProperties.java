package com.devnexus.frauddetection.infrastructure.streams.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.fraud.rules")
public record FraudRulesProperties(
    double maxTravelSpeedKmh,
    long maxTransactionsPerWindow,
    long velocityWindowMinutes
) {

}
