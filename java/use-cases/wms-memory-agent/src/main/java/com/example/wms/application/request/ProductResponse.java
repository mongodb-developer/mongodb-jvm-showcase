package com.example.wms.application.request;

import com.example.wms.domain.model.Product;

public record ProductResponse(
		String sku,
		String name
) {
	public static ProductResponse fromModel(Product product) {
		return new ProductResponse(product.sku(), product.name());
	}
}