package com.devrel.wms.event;

import com.devrel.wms.agent.AgentDefinition;
import com.devrel.wms.agent.AgentRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class ReplenishmentAnalysisListener {

	private final Logger logger = LoggerFactory.getLogger(ReplenishmentAnalysisListener.class);
	private final AgentRunner agentRunner;
	private final AgentDefinition replenishmentAgent;

	ReplenishmentAnalysisListener(AgentRunner agentRunner, AgentDefinition replenishmentAgent) {
		this.agentRunner = agentRunner;
		this.replenishmentAgent = replenishmentAgent;
	}

	@Async
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void analyze(OutboundInvoiceCompleted event) {
		String number = event.number();

		logger.info("Starting replenishment analysis for invoice {}", number);

		try {
			agentRunner.run(replenishmentAgent, number);
		} catch (Exception exception) {
			logger.error("Replenishment analysis failed for invoice {}", number, exception);
		}
	}
}
