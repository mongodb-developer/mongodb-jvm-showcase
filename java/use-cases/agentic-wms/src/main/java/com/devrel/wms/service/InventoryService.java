package com.devrel.wms.service;

import com.devrel.wms.entity.Inventory;
import com.devrel.wms.repository.InventoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class InventoryService {

	private final Logger logger = LoggerFactory.getLogger(InventoryService.class);
	InventoryRepository inventoryRepository;

	InventoryService(InventoryRepository inventoryRepository) {
		this.inventoryRepository = inventoryRepository;
	}

	public Inventory save(Inventory inventory) {
		Inventory save = inventoryRepository.save(inventory);

		logger.info("Inventory for product code {} saved", save.productCode());

		return save;
	}

	public Inventory findByProductCode(String productCode) {
		return inventoryRepository.findByProductCode(productCode).orElse(null);
	}

	public String add(String productCode, int quantity) {
		long updatedCount = inventoryRepository.add(productCode, quantity);

		if (updatedCount == 0) {
			return "Inventory not updated. Product not found or insufficient stock.";
		}

		return updatedCount + " warehouse(s) updated successfully.";
	}

}
