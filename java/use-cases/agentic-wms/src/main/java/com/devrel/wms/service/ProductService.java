package com.devrel.wms.service;

import com.devrel.wms.domain.Product;
import com.devrel.wms.exception.NotFoundException;
import com.devrel.wms.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

	private final Logger logger = LoggerFactory.getLogger(ProductService.class);
	ProductRepository productRepository;

	ProductService(ProductRepository productRepository) {
		this.productRepository = productRepository;
	}

	public Product save(Product product) {
		validate(product);

		Product save = productRepository.save(product);

		logger.info("Product with Id {} saved", save.id());

		return save;
	}

	public Product update(String id, Product product) {
		Product current = productRepository.findById(id)
				.orElseThrow(() -> new NotFoundException("Product not found: " + id));

		Product merged = new Product(
				current.id(),
				product.name() == null ? current.name() : product.name(),
				current.code());

		validate(merged);

		Product saved = productRepository.save(merged);

		logger.info("Product with Id {} updated", saved.id());

		return saved;
	}

	public List<Product> findAll() {
		return productRepository.findAll();
	}

	public Product findById(String id) {
		return productRepository.findById(id).orElse(null);
	}

	public Product findByCode(String code) {
		return productRepository.findByCode(code).orElse(null);
	}

	private void validate(Product product) {
		if (product.code() == null || product.code().isBlank()) {
			throw new IllegalArgumentException("Product code is required");
		}

		if (product.name() == null || product.name().isBlank()) {
			throw new IllegalArgumentException("Product name is required");
		}
	}

}
