package com.devrel.wms.knowledge;

import org.springframework.ai.document.Document;

import java.util.HashMap;
import java.util.Map;

public record DepositorKnowledgeEntry(
        String depositorId,
        KnowledgeType type,
        String text,
        Map<String, Object> attributes
) {

	public Document toDocument() {
		Map<String, Object> metadata = new HashMap<>(attributes == null ? Map.of() : attributes);
		metadata.put("depositorId", depositorId);
		metadata.put("type", type.name());

		return new Document(text, metadata);
	}

	private String documentId() {
		return depositorId + ":" + type.name();
	}
}
