package com.devrel.wms.tool;

import com.devrel.wms.knowledge.DepositorKnowledgeStore;
import com.devrel.wms.knowledge.KnowledgeType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class DepositorPolicyTool {

	private static final int TOP_K = 3;
	private static final List<KnowledgeType> REPLENISHMENT_TYPES =
			List.of(KnowledgeType.REPLENISHMENT, KnowledgeType.INBOUND, KnowledgeType.GENERAL);

	private final Logger logger = LoggerFactory.getLogger(DepositorPolicyTool.class);
	private final DepositorKnowledgeStore depositorKnowledgeStore;

	DepositorPolicyTool(DepositorKnowledgeStore depositorKnowledgeStore) {
		this.depositorKnowledgeStore = depositorKnowledgeStore;
	}

	@Tool(description = """
    	Search the replenishment policies agreed with a depositor, such as minimum order
    	quantity, lead time, packaging rules, blackout periods and approval thresholds.
    	Always use this tool before choosing a replenishment quantity, asking a natural
    	language question about what you need to know.
    """)
	public String getDepositorPolicies(
			@ToolParam(description = ProductCodes.DEPOSITOR_CODE_PARAM) String depositorCode,

			@ToolParam(description = "Question about the depositor policies, for example "
					+ "'What are the constraints to request 250 units today?'")
			String question
	) {
		logger.info("##TOOL## - Searching policies of depositor {} for: {}", depositorCode, question);

		List<Document> documents = depositorKnowledgeStore.search(
				question, depositorCode, REPLENISHMENT_TYPES, TOP_K);

		if (documents.isEmpty()) {
			return "No policy registered for depositor " + depositorCode
					+ ". Use standard replenishment criteria.";
		}

		return documents.stream()
				.map(document -> "- " + document.getText())
				.collect(Collectors.joining("\n\n"));
	}
}
