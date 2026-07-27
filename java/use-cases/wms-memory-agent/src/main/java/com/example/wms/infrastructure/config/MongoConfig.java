package com.example.wms.infrastructure.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.mongodb.autoconfigure.MongoClientSettingsBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MongoConfig {

	@Value("${devrel.application.track-name}")
	private String applicationName;

	@Bean
	MongoClientSettingsBuilderCustomizer applicationNameCustomizer() {
		return builder -> builder.applicationName(applicationName);
	}
}