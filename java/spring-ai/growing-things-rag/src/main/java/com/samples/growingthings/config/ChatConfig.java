package com.samples.growingthings.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.memory.repository.mongo.MongoChatMemoryRepository;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class ChatConfig {

	@Bean
	ChatMemory chatMemory(MongoChatMemoryRepository repository) {
		return MessageWindowChatMemory.builder()
				.chatMemoryRepository(repository)
				.maxMessages(5)
				.build();
	}

//	@Bean
//	InMemoryChatMemoryRepository inMemoryChatMemoryRepository() {
//		return new InMemoryChatMemoryRepository();
//	}

	@Bean
	@Primary
	ChatClient buildOpenAI(OpenAiChatModel openAiChatModel, ChatMemory chatMemory, VectorStore vectorStore) {
		return ChatClient.builder(openAiChatModel)
				.defaultSystem(
						"Answer only if the information is available in the retrieved documents." +
						"  If you don't know the answer, simply say:\n" +
						"  I don't have enough information to answer that question.")
				.defaultAdvisors(
						MessageChatMemoryAdvisor.builder(chatMemory).build(),
						QuestionAnswerAdvisor.builder(vectorStore).build(),
						SimpleLoggerAdvisor.builder().build()
				)
				.build();
	}

	@Bean
	ChatClient buildOllama(OllamaChatModel ollamaChatModel) {
		return ChatClient.builder(ollamaChatModel).build();
	}
}
