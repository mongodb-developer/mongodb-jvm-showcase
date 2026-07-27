package com.example.wms.infrastructure.database.entity;

import com.example.wms.domain.model.Product;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "products")
public class ProductDocument {

	@Indexed(unique = true)
	String sku;
	String name;
	String description;
	Boolean active;

	public ProductDocument(String sku, String name, String description, Boolean active) {
		this.sku = sku;
		this.name = name;
		this.description = description;
		this.active = active;
	}

	public static ProductDocument fromModel(Product product) {
		return new ProductDocument(
				product.sku(),
				product.name(),
				product.description(),
				product.active()
		);
	}

	public String getSku() {
		return sku;
	}

	public String getName() {
		return name;
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
