package com.devnexus.frauddetection;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@ConfigurationPropertiesScan
@EnableKafka
@EnableScheduling
public class FraudDetectionApplication {

	public static void main(String[] args) {
		SpringApplication.run(FraudDetectionApplication.class, args);
	}

}
