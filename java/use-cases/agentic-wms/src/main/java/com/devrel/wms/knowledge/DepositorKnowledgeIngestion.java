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
					"lead-time",
					KnowledgeType.REPLENISHMENT,
					"""
					Braspress delivers to our warehouse in 2 business days on average.
					During December the lead time extends to 5 business days due to peak season,
					so replenishment must be anticipated.""",
					Map.of("leadTimeDays", 2, "peakLeadTimeDays", 5)
			),
			new DepositorKnowledgeEntry(
					"bsp",
					"blackout-window",
					KnowledgeType.REPLENISHMENT,
					"""
					Braspress does not accept replenishment requests between the 25th and the last
					day of each month, when they run their inventory closing. Requests identified
					during this window must be reported as pending until the next month starts.""",
					Map.of("blackoutFromDayOfMonth", 25)
			),
			new DepositorKnowledgeEntry(
					"bsp",
					"packaging",
					KnowledgeType.INBOUND,
					"""
					All Braspress products arrive on standard pallets of 40 units.
					Replenishment quantities should be rounded up to a full pallet whenever possible
					to avoid handling costs.""",
					Map.of("unitsPerPallet", 40)
			),
			new DepositorKnowledgeEntry(
					"bsp",
					"approval-rules",
					KnowledgeType.GENERAL,
					"""
					Replenishment notifications for Braspress must be addressed to the logistics
					coordinator. Requests above 1000 units also require approval from the commercial
					manager before the shipment is arranged.""",
					Map.of("managerApprovalThreshold", 1000)
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
					"lead-time",
					KnowledgeType.REPLENISHMENT,
					"""
					Amazon delivers to our warehouse in 5 business days on average.
					Requests created on Friday are only processed on the following Monday.""",
					Map.of("leadTimeDays", 5)
			),
			new DepositorKnowledgeEntry(
					"amz",
					"packaging",
					KnowledgeType.INBOUND,
					"""
					Amazon products arrive on mixed pallets of 100 units.
					Partial pallets are accepted but generate an additional handling fee.""",
					Map.of("unitsPerPallet", 100)
			),
			new DepositorKnowledgeEntry(
					"amz",
					"approval-rules",
					KnowledgeType.GENERAL,
					"""
					Amazon replenishment notifications must be sent to the account manager and
					always include the expected delivery date. No manager approval is required
					regardless of the requested quantity.""",
					Map.of()
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
