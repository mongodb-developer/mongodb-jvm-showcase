package com.devrel.wms.service;

import com.devrel.wms.domain.DepositorRef;
import com.devrel.wms.domain.Inventory;
import com.devrel.wms.exception.ConflictException;
import com.devrel.wms.repository.InventoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.List;

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

	public List<Inventory> findAll() {
		return inventoryRepository.findAll();
	}

	public List<Inventory> findByProductCode(String productCode) {
		return inventoryRepository.findByProductCode(productCode);
	}

	public List<Inventory> findByDepositor(String depositorId) {
		return inventoryRepository.findByDepositorId(depositorId);
	}

	public Inventory findByProductCodeAndDepositor(String productCode, String depositorId) {
		return inventoryRepository.findByProductCodeAndDepositorId(productCode, depositorId).orElse(null);
	}

	public void add(String productCode, DepositorRef depositor, int quantity) {
		if (depositor == null || depositor.id() == null) {
			throw new IllegalArgumentException("Depositor is required for product code: " + productCode);
		}

		if (quantity >= 0) {
			mongoTemplate.upsert(
					new Query(Criteria.where("productCode").is(productCode)
							.and("depositor.id").is(depositor.id())),
					new Update().inc("quantity", quantity)
						.setOnInsert("depositor.name", depositor.name()),
					Inventory.class
			);

			logger.info("Inventory for product code {} and depositor {} increased by {}",
					productCode, depositor.id(), quantity);

			return;
		}

		if (inventoryRepository.add(productCode, depositor.id(), quantity) == 0) {
			throw new ConflictException("Insufficient stock for product code " + productCode
					+ " and depositor " + depositor.id());
		}

		logger.info("Inventory for product code {} and depositor {} decreased by {}",
				productCode, depositor.id(), -quantity);
	}

}
