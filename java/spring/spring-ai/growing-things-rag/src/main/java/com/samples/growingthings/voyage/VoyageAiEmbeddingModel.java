package com.samples.growingthings.voyage;

import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.Embedding;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingRequest;
import org.springframework.ai.embedding.EmbeddingResponse;

import java.util.List;

public class VoyageAiEmbeddingModel implements EmbeddingModel {

	private static final String INPUT_TYPE_DOCUMENT = "document";

	private final VoyageEmbeddingsClient client;
	private final String model;

	public VoyageAiEmbeddingModel(VoyageEmbeddingsClient client, String model) {
		this.client = client;
		this.model = model;
	}

	@Override
	public EmbeddingResponse call(EmbeddingRequest embeddingRequest) {
		var response = client.embed(new VoyageEmbeddingsClient.EmbeddingsRequest(
				embeddingRequest.getInstructions(),
				model,
				INPUT_TYPE_DOCUMENT
		));

		List<Embedding> embeddings = response.data().stream()
				.map(item -> new Embedding(item.embedding(), item.index()))
				.toList();

		return new EmbeddingResponse(embeddings);
	}

	@Override
	public float[] embed(Document document) {
		return embed(document.getText());
	}
}
