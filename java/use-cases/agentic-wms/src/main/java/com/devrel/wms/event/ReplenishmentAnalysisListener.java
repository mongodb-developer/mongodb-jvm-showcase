package com.devrel.wms.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.LocalDateTime;

@Component
public class ReplenishmentAnalysisListener {

	private final Logger logger = LoggerFactory.getLogger(ReplenishmentAnalysisListener.class);
	private final ChatClient chatClient;

	ReplenishmentAnalysisListener(ChatClient chatClient) {
		this.chatClient = chatClient;
	}

	@Async
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void analyze(OutboundInvoiceCompleted event) {
		String number = event.number();

		logger.info("Starting replenishment analysis for invoice {}", number);

		try {
			String response = chatClient
					.prompt()
					.system(system -> system
							.param("current_date", LocalDateTime.now()))
					.user("""
                Outbound invoice %s has just been completed.

                Analyze this invoice and determine whether any product requires replenishment.
                Use the available tools autonomously.
                Do not ask the user for additional information.
                If replenishment is necessary, create it.
                After creating a replenishment, notify the depositor using the notification tool.
                If not, take no action.
                """.formatted(number))
					.call()
					.content();

			logger.info("Replenishment analysis for invoice {} finished. Content: {}", number, response);
		} catch (Exception exception) {
			logger.error("Replenishment analysis failed for invoice {}", number, exception);
		}
	}
}
