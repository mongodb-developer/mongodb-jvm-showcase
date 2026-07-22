package com.devnexus.frauddetection.infrastructure.message.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaTopicsConfig {

	private final TopicsProperties topics;

	public KafkaTopicsConfig(TopicsProperties topics) {
		this.topics = topics;
	}

	@Bean
	public NewTopic transactionsTopic() {
		return new NewTopic(topics.transactions(), 3, (short) 1);
	}

	@Bean
	public NewTopic suspiciousTopic() {
		return new NewTopic(topics.suspicious(), 3, (short) 1);
	}

	@Bean
	public NewTopic toScoreTopic() {
		return new NewTopic(topics.toScore(), 3, (short) 1);
	}

	@Bean
	public NewTopic dlqTopic() {
		return new NewTopic(topics.dlq(), 3, (short) 1);
	}

}
