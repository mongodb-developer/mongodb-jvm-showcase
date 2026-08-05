package com.example.policyAgent.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "summary")
public record SummaryProperties(
		int maxTokens,
		int turnsToKeep
) {
}