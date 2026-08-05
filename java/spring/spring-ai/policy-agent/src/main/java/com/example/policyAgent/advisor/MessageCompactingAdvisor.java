package com.example.policyAgent.advisor;

import com.example.policyAgent.config.SummaryProperties;
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


			## Earlier conversation summaries

			These are a faithful record of the earlier part of this conversation, in
			chronological order. Treat what the user said here as true, and use it to
			answer questions about the user or about what was already discussed.
			They are not a source of company policy: any policy mentioned here must
			be confirmed by the retrieved documents.

			%s""";

	private final ChatMemory chatMemory;
	private final ChatMemoryRepository chatMemoryRepository;
	private final ChatClient summaryClient;
	private final SummaryConversationRepository summaryConversationRepository;
	private final SummaryProperties summaryProperties;
	private final TokenService tokenService;

	public MessageCompactingAdvisor(
			ChatMemory chatMemory,
			ChatMemoryRepository chatMemoryRepository,
			@Qualifier("summaryChatClient") ChatClient summaryClient,
			SummaryConversationRepository summaryConversationRepository,
			SummaryProperties summaryProperties,
			TokenService tokenService
	) {
		this.chatMemory = chatMemory;
		this.chatMemoryRepository = chatMemoryRepository;
		this.summaryClient = summaryClient;
		this.summaryConversationRepository = summaryConversationRepository;
		this.summaryProperties = summaryProperties;
		this.tokenService = tokenService;
	}

	@Override
	public ChatClientResponse adviseCall(ChatClientRequest chatClientRequest, CallAdvisorChain callAdvisorChain) {

		String conversationId = (String) chatClientRequest.context()
				.getOrDefault(ChatMemory.CONVERSATION_ID, DEFAULT_CONVERSATION_ID);

		// Compact first, so a chunk created now is already part of what goes to the model.
		checkAndCompact(conversationId);

		return callAdvisorChain.nextCall(
				withSummaryInSystemPrompt(chatClientRequest, loadSummaries(conversationId)));
	}

	private @Nullable String loadSummaries(String conversationId) {

		List<SummaryConversation> chunks =
				summaryConversationRepository.findByConversationIdOrderBySequenceAsc(conversationId);

		if (chunks.isEmpty()) {
			return null;
		}

		return chunks.stream()
				.map(chunk -> """
						<conversation-summary messages="%d-%d">
						%s
						</conversation-summary>""".formatted(
						chunk.getFromMessage(), chunk.getToMessage(), chunk.getSummary()))
				.collect(Collectors.joining("\n\n"));
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

	private void checkAndCompact(String conversationId) {

		List<Message> messages = chatMemory.get(conversationId);

		if (!isMaxTokensReached(messages)) {
			return;
		}

		int splitPosition = findStartOfRecentTurns(messages, summaryProperties.turnsToKeep());

		if (splitPosition == 0) {
			return;
		}

		List<Message> oldMessages = messages.subList(0, splitPosition);
		List<Message> recentMessages = messages.subList(splitPosition, messages.size());

		SummaryConversation lastChunk = summaryConversationRepository
				.findFirstByConversationIdOrderBySequenceDesc(conversationId)
				.orElse(null);

		int sequence = lastChunk == null ? 1 : lastChunk.getSequence() + 1;
		int fromMessage = lastChunk == null ? 1 : lastChunk.getToMessage() + 1;
		int toMessage = fromMessage + oldMessages.size() - 1;

		String summary = summarize(oldMessages);

		logger.info("Compacted conversation {}: chunk {} covering messages {}-{}",
				conversationId, sequence, fromMessage, toMessage);

		summaryConversationRepository.save(new SummaryConversation(
				conversationId, sequence, fromMessage, toMessage, summary
		));

		chatMemoryRepository.saveAll(conversationId, new ArrayList<>(recentMessages));
	}

	private String summarize(List<Message> oldMessages) {

		String transcript = oldMessages.stream()
				.map(message -> "%s: %s".formatted(message.getMessageType(), message.getText()))
				.collect(Collectors.joining("\n\n"));

		return summaryClient.prompt()
				.user("""
						<conversation-messages>
						%s
						</conversation-messages>
						""".formatted(transcript))
				.call()
				.content();
	}

	private boolean isMaxTokensReached(List<Message> messages) {
		int tokenCount = messages.stream()
				.mapToInt(message -> tokenService.count(message.getText()))
				.sum();

		logger.info("Token count: {}", tokenCount);

		return tokenCount > summaryProperties.maxTokens();
	}

	private int findStartOfRecentTurns(List<Message> messages, int turnsToKeep) {
		int userTurnsFound = 0;

		for (int index = messages.size() - 1; index >= 0; index--) {

			if (messages.get(index).getMessageType() == MessageType.USER) {
				userTurnsFound++;

				if (userTurnsFound == turnsToKeep) {
					return index;
				}
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