package com.example.policyAgent.service;

import com.example.policyAgent.advisor.MessageCompactingAdvisor;
import com.example.policyAgent.model.ChatRequest;
import com.example.policyAgent.model.ConversationHistory;
import com.example.policyAgent.model.ConversationListItem;
import com.example.policyAgent.model.SummaryConversation;
import com.example.policyAgent.repository.SummaryConversationRepository;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.MessageType;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatService {

	private final ChatClient chatClient;
	private final MessageCompactingAdvisor messageCompactingAdvisor;
	private final ChatMemory chatMemory;
	private final ChatMemoryRepository chatMemoryRepository;
	private final SummaryConversationRepository summaryConversationRepository;

	public ChatService(
			@Qualifier("chatClient") ChatClient chatClient,
			MessageCompactingAdvisor messageCompactingAdvisor,
			ChatMemory chatMemory,
			ChatMemoryRepository chatMemoryRepository,
			SummaryConversationRepository summaryConversationRepository
	) {
		this.chatClient = chatClient;
		this.messageCompactingAdvisor = messageCompactingAdvisor;
		this.chatMemory = chatMemory;
		this.chatMemoryRepository = chatMemoryRepository;
		this.summaryConversationRepository = summaryConversationRepository;
	}

	public String chat(ChatRequest chatRequest) {
		return chatClient.prompt()
				.user(chatRequest.message())
				.advisors(advisor -> advisor
						.param(ChatMemory.CONVERSATION_ID, chatRequest.conversationId())
						.advisors(messageCompactingAdvisor))
				.call()
				.content();
	}

	public List<ConversationListItem> conversations() {
		return chatMemoryRepository.findConversationIds().stream()
				.map(id -> new ConversationListItem(id, firstUserMessage(id)))
				.toList();
	}

	public void delete(String conversationId) {
		chatMemoryRepository.deleteByConversationId(conversationId);
		summaryConversationRepository.deleteByConversationId(conversationId);
	}

	private String firstUserMessage(String conversationId) {
		return chatMemory.get(conversationId).stream()
				.filter(message -> message.getMessageType() == MessageType.USER)
				.map(Message::getText)
				.findFirst()
				.orElse("(no messages)");
	}

	public ConversationHistory history(String conversationId) {

		List<ConversationHistory.HistoryMessage> messages = chatMemory.get(conversationId).stream()
				.filter(message -> message.getMessageType() == MessageType.USER
						|| message.getMessageType() == MessageType.ASSISTANT)
				.map(message -> new ConversationHistory.HistoryMessage(
						message.getMessageType().getValue(),
						message.getText()
				))
				.toList();

		String summary = summaryConversationRepository.findByConversationId(conversationId)
				.map(SummaryConversation::getSummary)
				.orElse(null);

		return new ConversationHistory(conversationId, summary, messages);
	}
}
