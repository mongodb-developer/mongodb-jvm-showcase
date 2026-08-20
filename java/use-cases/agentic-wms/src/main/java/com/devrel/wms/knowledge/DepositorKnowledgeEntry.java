package com.devrel.wms.knowledge;

import org.springframework.ai.document.Document;

import java.util.HashMap;
import java.util.Map;

public record DepositorKnowledgeEntry(
        String depositorId,
        String key,
        KnowledgeType type,
        String text,
        Map<String, Object> attributes
) {

	public Document toDocument() {
		Map<String, Object> metadata = new HashMap<>(attributes == null ? Map.of() : attributes);
		metadata.put("depositorId", depositorId);
		metadata.put("type", type.name());
		metadata.put("key", key);

		return new Document(documentId(), text, metadata);
	}

	private String documentId() {
		return depositorId + ":" + key;
	}
}
