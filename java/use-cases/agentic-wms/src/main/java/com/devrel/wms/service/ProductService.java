package com.devrel.wms.service;

import com.devrel.wms.domain.Product;
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
		Product save = productRepository.save(product);

		logger.info("Product with Id {} saved", save.id());

		return save;
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

}
