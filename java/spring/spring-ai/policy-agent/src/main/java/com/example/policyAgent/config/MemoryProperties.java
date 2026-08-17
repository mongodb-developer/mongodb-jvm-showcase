package com.example.policyAgent.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "agent.memory")
public record MemoryProperties(
		int maxMessages,
		int maxTokens,
		int keepTokens,
		String summaryModel
) {
}
