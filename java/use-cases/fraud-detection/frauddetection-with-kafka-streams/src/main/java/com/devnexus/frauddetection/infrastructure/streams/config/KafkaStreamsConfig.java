package com.devnexus.frauddetection.infrastructure.streams.config;

import com.devnexus.frauddetection.domain.rules.ImpossibleTravelValidator;
import com.devnexus.frauddetection.domain.rules.VelocityCheckValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafkaStreams;

@Configuration
@EnableKafkaStreams
public class KafkaStreamsConfig {

	@Bean
	public ImpossibleTravelValidator impossibleTravelValidator(FraudRulesProperties properties) {
		return new ImpossibleTravelValidator(properties.maxTravelSpeedKmh());
	}

	@Bean
	public VelocityCheckValidator velocityCheckValidator(FraudRulesProperties properties) {
		return new VelocityCheckValidator(properties.maxTransactionsPerWindow(), properties.velocityWindowMinutes());
	}
}
