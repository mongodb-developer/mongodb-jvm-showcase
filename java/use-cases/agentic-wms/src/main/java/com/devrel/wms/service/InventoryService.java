package com.devrel.wms.service;

import com.devrel.wms.entity.Inventory;
import com.devrel.wms.repository.InventoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

@Service
public class InventoryService {

	private final Logger logger = LoggerFactory.getLogger(InventoryService.class);
	private final InventoryRepository inventoryRepository;
	private final MongoTemplate mongoTemplate;

	InventoryService(InventoryRepository inventoryRepository, MongoTemplate mongoTemplate) {
		this.inventoryRepository = inventoryRepository;
		this.mongoTemplate = mongoTemplate;
	}

	public Inventory save(Inventory inventory) {
		Inventory save = inventoryRepository.save(inventory);

		logger.info("Inventory for product code {} saved", save.productCode());

		return save;
	}

	public Inventory findByProductCode(String productCode) {
		return inventoryRepository.findByProductCode(productCode).orElse(null);
	}

	public void add(String productCode, int quantity) {
		if (quantity >= 0) {
			mongoTemplate.upsert(
					new Query(Criteria.where("productCode").is(productCode)),
					new Update().inc("quantity", quantity),
					Inventory.class
			);

			logger.info("Inventory for product code {} increased by {}", productCode, quantity);

			return;
		}

		if (inventoryRepository.add(productCode, quantity) == 0) {
			throw new IllegalStateException(
					"Insufficient stock or unknown product code: " + productCode);
		}

		logger.info("Inventory for product code {} decreased by {}", productCode, -quantity);
	}

}
