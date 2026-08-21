package com.devrel.wms.config;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class EmbeddingConfig {

	@Bean
	public EmbeddingModel voyageEmbeddingModel(@Value("${voyage.base-url}") String baseUrl,
			@Value("${voyage.api-key}") String apiKey,
			@Value("${voyage.model}") String model,
			@Value("${voyage.dimensions}") int dimensions) {

		RestClient restClient = RestClient.builder()
			.requestFactory(new SimpleClientHttpRequestFactory())
			.defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
			.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
			.build();

		return new VoyageEmbeddingModel(restClient, baseUrl, model, dimensions);
	}
}
