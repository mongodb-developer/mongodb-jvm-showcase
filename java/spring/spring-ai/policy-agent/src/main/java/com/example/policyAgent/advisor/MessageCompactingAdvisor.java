package com.example.policyAgent.advisor;

import com.example.policyAgent.config.MemoryProperties;
import com.example.policyAgent.model.SummaryConversation;
import com.example.policyAgent.repository.SummaryConversationRepository;
import com.example.policyAgent.service.TokenService;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.MessageType;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class MessageCompactingAdvisor implements CallAdvisor {

	private static final Logger logger = LoggerFactory.getLogger(MessageCompactingAdvisor.class);

	private static final String DEFAULT_CONVERSATION_ID = "default";

	private static final String SUMMARY_BLOCK = """

			## Earlier conversation summary

			This is a faithful record of the earlier part of this conversation.
			Treat what the user said here as true, and use it to answer questions
			about the user or about what was already discussed.
			It is not a source of company policy: any policy mentioned here must
			be confirmed by the retrieved documents.

			<conversation-summary>
			%s
			</conversation-summary>""";

	private final ChatMemory chatMemory;
	private final ChatMemoryRepository chatMemoryRepository;
	private final ChatClient summaryClient;
	private final SummaryConversationRepository summaryConversationRepository;
	private final MemoryProperties memoryProperties;
	private final TokenService tokenService;

	public MessageCompactingAdvisor(
			ChatMemory chatMemory,
			ChatMemoryRepository chatMemoryRepository,
			@Qualifier("summaryChatClient") ChatClient summaryClient,
			SummaryConversationRepository summaryConversationRepository,
			MemoryProperties memoryProperties,
			TokenService tokenService
	) {
		this.chatMemory = chatMemory;
		this.chatMemoryRepository = chatMemoryRepository;
		this.summaryClient = summaryClient;
		this.summaryConversationRepository = summaryConversationRepository;
		this.memoryProperties = memoryProperties;
		this.tokenService = tokenService;
	}

	@Override
	public ChatClientResponse adviseCall(ChatClientRequest chatClientRequest, CallAdvisorChain callAdvisorChain) {

		String conversationId = (String) chatClientRequest.context()
				.getOrDefault(ChatMemory.CONVERSATION_ID, DEFAULT_CONVERSATION_ID);

		// Compact first, so a chunk created now is already part of what goes to the model.
		return callAdvisorChain.nextCall(
				withSummaryInSystemPrompt(chatClientRequest, compactIfNeeded(conversationId)));
	}

	private ChatClientRequest withSummaryInSystemPrompt(ChatClientRequest request, @Nullable String summary) {

		if (summary == null || summary.isBlank()) {
			return request;
		}

		Prompt augmentedPrompt = request.prompt().augmentSystemMessage(
				systemMessage -> systemMessage.mutate()
						.text(Objects.toString(systemMessage.getText(), "") + SUMMARY_BLOCK.formatted(summary))
						.build()
		);

		return request.mutate().prompt(augmentedPrompt).build();
	}

	private @Nullable String compactIfNeeded(String conversationId) {

		SummaryConversation existing = summaryConversationRepository
				.findByConversationId(conversationId)
				.orElse(null);

		String previousSummary = existing == null ? null : existing.getSummary();

		List<Message> messages = chatMemory.get(conversationId);

		if (!isMaxTokensReached(messages)) {
			return previousSummary;
		}

		int splitPosition = findSplitByTokenBudget(messages, memoryProperties.keepTokens());

		if (splitPosition == 0) {
			return previousSummary;
		}

		List<Message> oldMessages = messages.subList(0, splitPosition);
		List<Message> recentMessages = messages.subList(splitPosition, messages.size());

		String summary = summarize(previousSummary, oldMessages);

		SummaryConversation toSave;

		if (existing == null) {
			toSave = new SummaryConversation(conversationId, summary, oldMessages.size());
		}
		else {
			existing.update(summary, oldMessages.size());
			toSave = existing;
		}

		logger.info("Compacted conversation {}: summary now covers {} messages",
				conversationId, toSave.getMessagesSummarized());

		summaryConversationRepository.save(toSave);

		chatMemoryRepository.saveAll(conversationId, new ArrayList<>(recentMessages));

		return summary;
	}

	private String summarize(@Nullable String previousSummary, List<Message> oldMessages) {

		String transcript = oldMessages.stream()
				.map(message -> "%s: %s".formatted(message.getMessageType(), message.getText()))
				.collect(Collectors.joining("\n\n"));

		String previousBlock = previousSummary == null || previousSummary.isBlank()
				? "(none, this is the first summary of this conversation)"
				: previousSummary;

		return summaryClient.prompt()
				.user("""
						<previous-summary>
						%s
						</previous-summary>

						<new-messages>
						%s
						</new-messages>
						""".formatted(previousBlock, transcript))
				.call()
				.content();
	}

	private boolean isMaxTokensReached(List<Message> messages) {
		int tokenCount = messages.stream()
				.mapToInt(message -> tokenService.count(message.getText()))
				.sum();

		logger.info("Token count: {}", tokenCount);

		return tokenCount > memoryProperties.maxTokens();
	}

	private int findSplitByTokenBudget(List<Message> messages, int keepTokens) {
		int tokenCount = 0;
		int splitPosition = messages.size();

		for (int index = messages.size() - 1; index >= 0; index--) {
			tokenCount += tokenService.count(messages.get(index).getText());

			if (tokenCount > keepTokens) {
				break;
			}

			if (messages.get(index).getMessageType() == MessageType.USER) {
				splitPosition = index;
			}
		}

		return splitPosition == messages.size()
				? findLastUserMessage(messages)
				: splitPosition;
	}

	private int findLastUserMessage(List<Message> messages) {
		for (int index = messages.size() - 1; index >= 0; index--) {
			if (messages.get(index).getMessageType() == MessageType.USER) {
				return index;
			}
		}

		return 0;
	}

	@Override
	public String getName() {
		return this.getClass().getSimpleName();
	}

	@Override
	public int getOrder() {
		// Run before.
		return Advisor.DEFAULT_CHAT_MEMORY_PRECEDENCE_ORDER - 100;
	}
}