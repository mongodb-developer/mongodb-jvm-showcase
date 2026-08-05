package com.example.policyAgent.config;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.mongodb.autoconfigure.MongoClientSettingsBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MongoConfig {

	@Value("${devrel.application.track-name}")
	private String applicationName;

	@Value("${spring.mongodb.uri}")
	private String connectionString;

	@Bean
	MongoClientSettingsBuilderCustomizer applicationNameCustomizer() {
		return builder -> builder.applicationName(applicationName);
	}

	@Bean
	public MongoClient mongoClient() {
		var settings = MongoClientSettings
				.builder()
				.applyConnectionString(new ConnectionString(connectionString))
				.build();
		return MongoClients.create(settings);
	}
}
