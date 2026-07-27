package com.example.wms.infrastructure.http;

import com.example.wms.application.CreateProductUseCase;
import com.example.wms.application.request.ProductRequest;
import com.example.wms.application.request.ProductResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/products")
public class ProductController {

	private final CreateProductUseCase createProductUseCase;

	ProductController(CreateProductUseCase createProductUseCase) {
		this.createProductUseCase = createProductUseCase;
	}

	@PostMapping
	public ProductResponse create(@RequestBody ProductRequest request) {
		return createProductUseCase.execute(request);
	}
}
