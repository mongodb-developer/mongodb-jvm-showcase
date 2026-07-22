package com.devnexus.frauddetection.infrastructure.message.config;

import com.devnexus.frauddetection.domain.model.Transaction;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

@Configuration
public class KafkaTemplateConfig {

	@Bean
	public KafkaTemplate<String, Transaction> transactionKafkaTemplate(ProducerFactory<String, Transaction> producerFactory) {
		return new KafkaTemplate<>(producerFactory);
	}
}
