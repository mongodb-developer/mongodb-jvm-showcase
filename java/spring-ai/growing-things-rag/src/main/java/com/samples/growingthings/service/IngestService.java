package com.samples.growingthings.service;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class IngestService {

	private final VectorStore vectorStore;

	IngestService(VectorStore vectorStore) {
		this.vectorStore = vectorStore;
	}

	public Map<String, Object> ingest() {
		var documents = List.of(
				new Document("Tomatoes grow best in full sun. Plant them in good soil, water them two or three times a week, and use a cage to support the plant."),
				new Document("Roses need full sun and regular watering. Prune old branches every spring and use fertilizer to help flowers grow."),
				new Document("A garden needs sun, water, and good soil. Remove weeds often and water the plants every few days."),
				new Document("A dog needs food, water, exercise, and love. Visit the veterinarian to keep it healthy."),
		 		new Document("A child grows well with healthy food, education, love, and a safe home. Reading and daily routines help children learn and feel confident.")
		);

		vectorStore.add(documents);

		return Map.of("added", documents.size(), "documents", documents);
	}
}
