package com.devrel.wms.knowledge;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class DepositorKnowledgeIngestion {

	private static final List<DepositorKnowledgeEntry> ENTRIES = List.of(
			new DepositorKnowledgeEntry(
					"bsp",
					"replenishment-minimum",
					KnowledgeType.REPLENISHMENT,
					"""
					Replenishment orders for Braspress must contain at least 100 units per product.
					Orders below this threshold are rejected by their logistics team and must be
					consolidated with the next replenishment cycle.""",
					Map.of("minimumQuantity", 100)
			),
			new DepositorKnowledgeEntry(
					"bsp",
					"replenishment-leadtime",
					KnowledgeType.REPLENISHMENT,
					"""
					Braspress delivers to our warehouse in 2 business days on average.
					During December the lead time extends to 5 business days due to peak season,
					so replenishment must be anticipated.""",
					Map.of("leadTimeDays", 2, "peakLeadTimeDays", 5)
			),
			new DepositorKnowledgeEntry(
					"amz",
					"replenishment-minimum",
					KnowledgeType.REPLENISHMENT,
					"""
					Replenishment orders for Amazon must contain at least 500 units per product.
					Amazon consolidates smaller requests and may delay them by an entire week.""",
					Map.of("minimumQuantity", 500)
			),
			new DepositorKnowledgeEntry(
					"amz",
					"replenishment-leadtime",
					KnowledgeType.REPLENISHMENT,
					"""
					Amazon delivers to our warehouse in 5 business days on average.
					Requests created on Friday are only processed on the following Monday.""",
					Map.of("leadTimeDays", 5)
			)
	);

	private final Logger logger = LoggerFactory.getLogger(DepositorKnowledgeIngestion.class);
	private final DepositorKnowledgeStore depositorKnowledgeStore;
	private final DepositorKnowledgeRepository depositorKnowledgeRepository;

	DepositorKnowledgeIngestion(
			DepositorKnowledgeStore depositorKnowledgeStore,
			DepositorKnowledgeRepository depositorKnowledgeRepository) {
		this.depositorKnowledgeStore = depositorKnowledgeStore;
		this.depositorKnowledgeRepository = depositorKnowledgeRepository;
	}

	public void ingest() {
		Set<String> existing = depositorKnowledgeRepository.findAll().stream()
				.map(entry -> entry.depositorId() + ":" + entry.key())
				.collect(Collectors.toSet());

		List<Document> documents = ENTRIES.stream()
				.map(DepositorKnowledgeEntry::toDocument)
				.filter(document -> !existing.contains(document.getId()))
				.toList();

		if (documents.isEmpty()) {
			logger.info("Depositor knowledge already ingested, keeping the {} stored entry(ies)", existing.size());

			return;
		}

		depositorKnowledgeStore.save(documents);

		logger.info("Depositor knowledge ingestion finished with {} entry(ies)", documents.size());
	}
}
