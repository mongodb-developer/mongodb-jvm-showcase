package com.example.showcase;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class AutomatedEmbeddingApplication {

	public static void main(String[] args) {
		SpringApplication.run(AutomatedEmbeddingApplication.class, args);
	}

}
