package com.example.policyAgent;

import com.example.policyAgent.config.MemoryProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(value = {MemoryProperties.class})
public class PolicyAgentApplication {

	public static void main(String[] args) {
		SpringApplication.run(PolicyAgentApplication.class, args);
	}

}
