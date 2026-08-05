package com.example.policyAgent.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "summary_conversation")
@CompoundIndex(name = "conversation_sequence", def = "{'conversationId': 1, 'sequence': 1}", unique = true)
public class SummaryConversation {

	@Id
	public String id;
	public String conversationId;
	public int sequence;
	public int fromMessage;
	public int toMessage;
	public String summary;
	public LocalDateTime createdAt;

	public SummaryConversation(
			String conversationId,
			int sequence,
			int fromMessage,
			int toMessage,
			String summary
	) {
		this.conversationId = conversationId;
		this.sequence = sequence;
		this.fromMessage = fromMessage;
		this.toMessage = toMessage;
		this.summary = summary;
		createdAt = LocalDateTime.now();
	}

	public String getSummary() {
		return summary;
	}

	public int getSequence() {
		return sequence;
	}

	public int getFromMessage() {
		return fromMessage;
	}

	public int getToMessage() {
		return toMessage;
	}
}
