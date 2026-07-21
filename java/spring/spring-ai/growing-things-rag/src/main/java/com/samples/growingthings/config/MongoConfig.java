package com.samples.growingthings.config;

import org.springframework.boot.mongodb.autoconfigure.MongoClientSettingsBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MongoConfig {

	@Bean
	MongoClientSettingsBuilderCustomizer applicationNameCustomizer() {
		return builder -> builder.applicationName("devrel-java-springai");
	}
}
