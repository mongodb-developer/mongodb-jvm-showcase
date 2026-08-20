package com.devrel.wms.repository;

import com.devrel.wms.domain.Inventory;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryRepository extends MongoRepository<Inventory, String> {
	List<Inventory> findByProductCode(String productCode);

	Optional<Inventory> findByProductCodeAndDepositorId(String productCode, String depositorId);

	@Query("{ 'productCode': ?0, 'depositor.id': ?1, '$expr': { '$gte': [ { '$add': [ '$quantity', ?2 ] }, 0 ] } }")
	@Update("{ '$inc': { 'quantity': ?2 } }")
	long add(String productCode, String depositorId, int quantity);
}
