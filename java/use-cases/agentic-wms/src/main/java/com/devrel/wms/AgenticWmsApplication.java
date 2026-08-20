package com.devrel.wms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class AgenticWmsApplication {

	public static void main(String[] args) {
		SpringApplication.run(AgenticWmsApplication.class, args);
	}

}
