package com.devnexus.frauddetection.infrastructure.message.producer;

import com.devnexus.frauddetection.domain.model.Transaction;
import com.devnexus.frauddetection.domain.port.TransactionProducerPort;
import com.devnexus.frauddetection.infrastructure.message.config.TopicsProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class TransactionProducer implements TransactionProducerPort {

	private static final Logger log = LoggerFactory.getLogger(TransactionProducer.class);

	private final KafkaTemplate<String, Transaction> kafkaTemplate;
	private final TopicsProperties topics;

	public TransactionProducer(KafkaTemplate<String, Transaction> kafkaTemplate, TopicsProperties topics) {
		this.kafkaTemplate = kafkaTemplate;
		this.topics = topics;
	}

	@Override
	public void send(Transaction transaction) {
		kafkaTemplate.send(topics.transactions(), transaction)
		.whenComplete((result, e) -> {
			if (e != null) {
				log.error("Error sending transaction", e);
			} else {
				log.info("Sent transactionId={} to topic={}",
					transaction.transactionId(), topics.transactions());
			}
		});
	}
}
