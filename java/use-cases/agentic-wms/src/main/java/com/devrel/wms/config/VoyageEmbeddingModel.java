package com.devrel.wms.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.Embedding;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingRequest;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;

import tools.jackson.databind.ObjectMapper;

public class VoyageEmbeddingModel implements EmbeddingModel {

	private final RestClient restClient;

	private final ObjectMapper objectMapper = new ObjectMapper();

	private final String url;

	private final String model;

	private final int dimensions;

	public VoyageEmbeddingModel(RestClient restClient, String url, String model, int dimensions) {
		this.restClient = restClient;
		this.url = url;
		this.model = model;
		this.dimensions = dimensions;
	}

	@Override
	public EmbeddingResponse call(EmbeddingRequest request) {
		List<String> instructions = request.getInstructions();
		List<Embedding> embeddings = new ArrayList<>(instructions.size());

		for (int i = 0; i < instructions.size(); i++) {
			embeddings.add(new Embedding(embedSingle(instructions.get(i)), i));
		}

		return new EmbeddingResponse(embeddings);
	}

	private float[] embedSingle(String text) {
		ResponseEntity<String> entity = this.restClient.post()
			.uri(this.url)
			.accept(MediaType.APPLICATION_JSON)
			.body(new VoyageRequest(this.model, text))
			.retrieve()
			.toEntity(String.class);

		String payload = entity.getBody();

		if (payload == null || payload.isBlank()) {
			throw new IllegalStateException(
					"Empty body from " + this.url + " status=" + entity.getStatusCode() + " headers=" + entity.getHeaders());
		}

		VoyageResponse response = this.objectMapper.readValue(payload, VoyageResponse.class);

		if (response.data() == null || response.data().isEmpty()) {
			throw new IllegalStateException("No embedding data from " + this.url + " payload=" + truncate(payload));
		}

		return response.data().getFirst().embedding();
	}

	private static String truncate(String value) {
		return value.length() <= 300 ? value : value.substring(0, 300);
	}

	@Override
	public float[] embed(Document document) {
		return embed(document.getText());
	}

	@Override
	public int dimensions() {
		return this.dimensions;
	}

	record VoyageRequest(String model, String input) {
	}

	record VoyageResponse(List<VoyageEmbedding> data) {
	}

	record VoyageEmbedding(float[] embedding, Integer index) {
	}
}
