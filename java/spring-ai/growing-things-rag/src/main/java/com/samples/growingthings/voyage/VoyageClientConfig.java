package com.samples.growingthings.voyage;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class VoyageClientConfig {

	@Bean
	@Primary
	EmbeddingModel voyageEmbeddingModel(
			VoyageEmbeddingsClient client,
			@Value("${voyage.model}") String model) {
		return new VoyageAiEmbeddingModel(client, model);
	}

	@Bean
	VoyageEmbeddingsClient voyageEmbeddingsClient(
			@Value("${voyage.api-key}") String apiKey,
			@Value("${voyage.url}") String url) {

		RestClient restClient = RestClient.builder()
				.baseUrl(url)
				.defaultHeader("Authorization", "Bearer " + apiKey)
				.build();

		return HttpServiceProxyFactory
				.builderFor(RestClientAdapter.create(restClient))
				.build()
				.createClient(VoyageEmbeddingsClient.class);
	}
}