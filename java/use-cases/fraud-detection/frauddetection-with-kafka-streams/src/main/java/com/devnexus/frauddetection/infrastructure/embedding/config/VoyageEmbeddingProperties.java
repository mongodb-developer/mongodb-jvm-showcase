package com.devnexus.frauddetection.infrastructure.embedding.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.embedding.voyage")
public record VoyageEmbeddingProperties(
        String apiKey,
        String model,
        String baseUrl,
	 	Integer outputDimension)
{}
