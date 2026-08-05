package com.example.policyAgent.model;

public record ChatRequest(
		String message,
		String conversationId
) {
}
