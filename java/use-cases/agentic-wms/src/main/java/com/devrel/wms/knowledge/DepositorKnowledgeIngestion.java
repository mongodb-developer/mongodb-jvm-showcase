package com.devrel.wms.knowledge;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class DepositorKnowledgeIngestion {

	private static final List<DepositorKnowledgeEntry> ENTRIES = List.of(
			new DepositorKnowledgeEntry(
					"braspress",
					KnowledgeType.REPLENISHMENT,
					"Replenishments must be requested with at least 100 boxes. The average lead time is 2 days.",
					Map.of(
							"minimumQuantity", 100,
							"leadTimeDays", 2
					)
			),
			new DepositorKnowledgeEntry(
					"amazon",
					KnowledgeType.REPLENISHMENT,
					"Replenishments must be requested with at least 500 boxes. The average lead time is 5 days.",
					Map.of(
							"minimumQuantity", 500,
							"leadTimeDays", 5
					)
			)
	);

	private final Logger logger = LoggerFactory.getLogger(DepositorKnowledgeIngestion.class);
	private final DepositorKnowledgeStore depositorKnowledgeStore;

	DepositorKnowledgeIngestion(DepositorKnowledgeStore depositorKnowledgeStore) {
		this.depositorKnowledgeStore = depositorKnowledgeStore;
	}

	public void ingest() {
		List<Document> documents = ENTRIES.stream()
				.map(DepositorKnowledgeEntry::toDocument)
				.toList();

		depositorKnowledgeStore.save(documents);

		logger.info("Depositor knowledge ingestion finished with {} entry(ies)", documents.size());
	}
}
