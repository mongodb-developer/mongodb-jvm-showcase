package com.example.showcase.config;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MongoConfig {

	@Value("${spring.mongodb.uri}")
	private String connectionString;

	@Bean
	MongoClient mongoClient() {
		return MongoClients.create(
				MongoClientSettings.builder()
						.applyConnectionString(new ConnectionString(connectionString))
						.applicationName("devrel-tutorial-java-automated-embedding")
						.build());
	}
}
