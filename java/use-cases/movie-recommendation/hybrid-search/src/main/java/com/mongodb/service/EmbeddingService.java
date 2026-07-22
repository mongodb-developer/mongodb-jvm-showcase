package com.mongodb.service;

import com.mongodb.dto.EmbeddingsRequest;
import com.mongodb.voyage.VoyageConfigProperties;
import com.mongodb.voyage.VoyageEmbeddingsClient;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.logging.Logger;

@Service
public class EmbeddingService {

	private final Logger logger = Logger.getLogger(EmbeddingService.class.getName());

	private final VoyageEmbeddingsClient client;
	private final VoyageConfigProperties config;

	public EmbeddingService(VoyageEmbeddingsClient client, VoyageConfigProperties config) {
		this.client = client;
		this.config = config;
	}

	@Cacheable("embeddings")
	public List<Double> embedQuery(
			String text) {

		logger.info("Generating embeddings .. ");

		var res = client.embed(new EmbeddingsRequest(
				List.of(text), config.model(), "query", config.outputDimension()));

		logger.info("Embeddings generated successfully!");

		return res.data().getFirst().embedding();
	}
}
