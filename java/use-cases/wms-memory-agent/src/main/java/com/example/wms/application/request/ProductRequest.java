package com.example.wms.application.request;

import com.example.wms.domain.model.Product;
import com.example.wms.infrastructure.database.entity.ProductDocument;

public record ProductRequest(
		String sku,
		String name,
		String description,
		Boolean active
) {
	public ProductRequest {
		if (active == null) {
			active = true;
		}
	}

	public Product toModel() {
		return new Product(
				sku,
				name,
				description,
				active
		);
	}

}