package com.samples.growingthings.service;

import com.samples.growingthings.model.GrowRecommendation;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.stereotype.Service;

@Service
public class GrowingService {

	public record Question(String message, String conversationId) {
	}

	private final ChatClient chatClient;

	GrowingService(ChatClient chatClient) {
		this.chatClient = chatClient;
	}

	public GrowRecommendation chat(Question question) {
		return chatClient
				.prompt(question.message())
				.advisors(a -> a
						.param(ChatMemory.CONVERSATION_ID, question.conversationId())
				).call()
				.entity(
						GrowRecommendation.class,
						ChatClient.EntityParamSpec::validateSchema
				);
	}
}
