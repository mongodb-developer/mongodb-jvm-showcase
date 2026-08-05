package com.example.policyAgent.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "summary_conversation")
public class SummaryConversation {

	@Id
	public String conversationId;
	public String summary;
	public int messagesSummarized;
	public LocalDateTime createdAt;
	public LocalDateTime updatedAt;

	public SummaryConversation(String conversationId, String summary, int messagesSummarized) {
		this.conversationId = conversationId;
		this.summary = summary;
		this.messagesSummarized = messagesSummarized;
		createdAt = LocalDateTime.now();
		updatedAt = createdAt;
	}

	public String getConversationId() {
		return conversationId;
	}

	public String getSummary() {
		return summary;
	}

	public int getMessagesSummarized() {
		return messagesSummarized;
	}

	public void update(String summary, int newlySummarizedMessages) {
		this.summary = summary;
		messagesSummarized += newlySummarizedMessages;
		updatedAt = LocalDateTime.now();
	}
}
