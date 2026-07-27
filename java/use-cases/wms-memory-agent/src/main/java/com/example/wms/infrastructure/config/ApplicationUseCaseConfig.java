package com.example.wms.infrastructure.config;

import com.example.wms.application.CreateProductUseCase;
import com.example.wms.domain.port.ProductPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationUseCaseConfig {

	@Bean
	public CreateProductUseCase createProductUseCase(ProductPort productPort) {
		return new CreateProductUseCase(productPort);
	}
}
