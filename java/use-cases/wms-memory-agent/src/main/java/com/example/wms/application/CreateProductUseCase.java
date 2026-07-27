package com.example.wms.application;

import com.example.wms.application.request.ProductRequest;
import com.example.wms.application.request.ProductResponse;
import com.example.wms.domain.port.ProductPort;

public class CreateProductUseCase {

	private final ProductPort productPort;

	public CreateProductUseCase(ProductPort productPort) {
		this.productPort = productPort;
	}

	public ProductResponse execute(ProductRequest request) {
		return ProductResponse.fromModel(productPort.save(request.toModel()));
	}
}
