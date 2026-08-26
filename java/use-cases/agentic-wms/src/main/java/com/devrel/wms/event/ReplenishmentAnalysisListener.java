package com.devrel.wms.event;

import com.devrel.wms.agent.AgentDefinition;
import com.devrel.wms.agent.AgentRunner;
import com.devrel.wms.limits.DemoLimits;
import com.devrel.wms.repository.AgentRunRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.LocalDate;

@Component
public class ReplenishmentAnalysisListener {

	private final Logger logger = LoggerFactory.getLogger(ReplenishmentAnalysisListener.class);
	private final AgentRunner agentRunner;
	private final AgentDefinition replenishmentAgentDefinition;
	private final AgentRunRepository agentRunRepository;
	private final DemoLimits limits;

	ReplenishmentAnalysisListener(
			AgentRunner agentRunner,
			AgentDefinition replenishmentAgentDefinition,
			AgentRunRepository agentRunRepository,
			DemoLimits limits) {
		this.agentRunner = agentRunner;
		this.replenishmentAgentDefinition = replenishmentAgentDefinition;
		this.agentRunRepository = agentRunRepository;
		this.limits = limits;
	}

	@Async
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void onOutboundInvoiceCompleted(OutboundInvoiceCompleted event) {
		String number = event.number();

		// THIS IS FOR RATE LIMIT.
		if (dailyBudgetExhausted()) {
			logger.warn("Daily agent run limit of {} reached. Skipping analysis for invoice {}",
					limits.agentRunsPerDay(), number);

			return;
		}

		logger.info("Starting replenishment analysis for invoice {}", number);

		try {
			agentRunner.run(replenishmentAgentDefinition, number);
		} catch (Exception exception) {
			logger.error("Replenishment analysis failed for invoice {}", number, exception);
		}
	}

	private boolean dailyBudgetExhausted() {
		if (!limits.enabled()) {
			return false;
		}

		return agentRunRepository.countByStartedAtGreaterThanEqual(LocalDate.now().atStartOfDay())
				>= limits.agentRunsPerDay();
	}
}
