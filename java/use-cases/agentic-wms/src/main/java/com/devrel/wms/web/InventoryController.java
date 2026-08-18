package com.devrel.wms.web;

import com.devrel.wms.entity.Inventory;
import com.devrel.wms.service.InventoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/inventory")
public class InventoryController {

	private final InventoryService inventoryService;

	InventoryController(InventoryService inventoryService) {
		this.inventoryService = inventoryService;
	}

	@PostMapping
	public ResponseEntity<Inventory> create(@RequestBody Inventory inventory) {
		Inventory created = inventoryService.save(inventory);

		return ResponseEntity
				.created(URI.create("/inventory/" + created.productCode()))
				.body(created);
	}

	@GetMapping("/{productCode}")
	public ResponseEntity<Inventory> findByProductCode(@PathVariable String productCode) {
		Inventory inventory = inventoryService.findByProductCode(productCode);

		if (inventory == null) {
			return ResponseEntity.notFound().build();
		}

		return ResponseEntity.ok(inventory);
	}

	@PostMapping("/{productCode}/movements")
	public ResponseEntity<Void> add(@PathVariable String productCode, @RequestParam int quantity) {
		inventoryService.add(productCode, quantity);

		return ResponseEntity.noContent().build();
	}
}
