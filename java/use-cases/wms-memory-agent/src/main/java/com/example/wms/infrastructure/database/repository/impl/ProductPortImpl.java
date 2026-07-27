package com.example.wms.infrastructure.database.repository.impl;

import com.example.wms.domain.model.Product;
import com.example.wms.domain.port.ProductPort;
import com.example.wms.infrastructure.database.entity.ProductDocument;
import com.example.wms.infrastructure.database.repository.ProductRepository;
import org.springframework.stereotype.Repository;

@Repository
public class ProductPortImpl implements ProductPort {

	private final ProductRepository productRepository;

	ProductPortImpl(ProductRepository productRepository) {
		this.productRepository = productRepository;
	}

	@Override
	public Product save(Product product) {
		ProductDocument productDocument = ProductDocument.fromModel(product);
		return productRepository.save(productDocument).toModel();
	}
}
