package com.devrel.wms.repository;

import com.devrel.wms.entity.Product;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends MongoRepository<Product, String> {
	Optional<Product> findByCode(String code);
}
