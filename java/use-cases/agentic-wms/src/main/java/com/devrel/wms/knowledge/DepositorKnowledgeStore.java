package com.devrel.wms.knowledge;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DepositorKnowledgeStore {

	private final Logger logger = LoggerFactory.getLogger(DepositorKnowledgeStore.class);
	private final VectorStore vectorStore;

	DepositorKnowledgeStore(VectorStore vectorStore) {
		this.vectorStore = vectorStore;
	}

	public void save(List<Document> documents) {
		vectorStore.add(documents);

		logger.info("Added {} knowledge document(s)", documents.size());
	}

	public List<Document> search(String query, String depositorId, int topK) {
		return vectorStore.similaritySearch(SearchRequest.builder()
				.query(query)
				.topK(topK)
				.filterExpression("depositorId == '" + depositorId + "'")
				.build());
	}
}
