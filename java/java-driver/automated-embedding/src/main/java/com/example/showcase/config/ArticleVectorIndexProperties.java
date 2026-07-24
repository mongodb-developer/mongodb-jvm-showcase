package com.example.showcase.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "article.vector-index")
public record ArticleVectorIndexProperties(
		String database,
		String collection,
		String name,
		String path,
		String modality,
		String model,
		int numCandidates,
		int readyTimeoutSeconds
) {
}
