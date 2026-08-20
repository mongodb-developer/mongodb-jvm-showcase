package com.devrel.wms.knowledge;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "wms.knowledge.ingest-on-startup", havingValue = "true", matchIfMissing = true)
public class DepositorKnowledgeBootstrap {

	private final Logger logger = LoggerFactory.getLogger(DepositorKnowledgeBootstrap.class);
	private final DepositorKnowledgeIngestion depositorKnowledgeIngestion;

	DepositorKnowledgeBootstrap(DepositorKnowledgeIngestion depositorKnowledgeIngestion) {
		this.depositorKnowledgeIngestion = depositorKnowledgeIngestion;
	}

	@EventListener(ApplicationReadyEvent.class)
	public void ingestOnStartup() {
		try {
			depositorKnowledgeIngestion.ingest();
		} catch (Exception exception) {
			logger.error("Depositor knowledge ingestion failed", exception);
		}
	}
}
