package com.example.wms.infrastructure.database.repository;

import com.example.wms.infrastructure.database.entity.ProductDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends MongoRepository<ProductDocument, String> {
}
