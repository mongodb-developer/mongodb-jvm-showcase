package com.devrel.wms.repository;

import com.devrel.wms.entity.Inventory;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InventoryRepository extends MongoRepository<Inventory, String> {
	Optional<Inventory> findByProductCode(String productCode);

	@Query("{ 'productCode': ?0, '$expr': { '$gte': [ { '$add': [ '$quantity', ?1 ] }, 0 ] } }")
	@Update("{ '$inc': { 'quantity': ?1 } }")
	long add(String productCode, int quantity);
}
