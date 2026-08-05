package com.example.policyAgent.model;

import java.util.List;

public record ConversationHistory(
		String conversationId,
		String summary,
		List<HistoryMessage> messages
) {

	public record HistoryMessage(String role, String text) {
	}
}
