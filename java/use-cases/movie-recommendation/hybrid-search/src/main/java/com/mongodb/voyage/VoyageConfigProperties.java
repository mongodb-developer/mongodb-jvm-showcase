package com.mongodb.voyage;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "voyage")
public record VoyageConfigProperties(
     String model,
     int outputDimension,
     String vectorIndexName,
	 String vectorCollectionName,
     String vectorField,
     int topK,
     int numCandidates,
     String baseUrl,
     String apiKey)
{}
