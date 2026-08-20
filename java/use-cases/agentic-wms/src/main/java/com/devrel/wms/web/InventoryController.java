package com.devrel.wms.web;

import com.devrel.wms.domain.Depositor;
import com.devrel.wms.domain.Inventory;
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
import java.util.List;

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

	@GetMapping
	public ResponseEntity<List<Inventory>> findAll() {
		return ResponseEntity.ok(inventoryService.findAll());
	}

	@GetMapping("/{productCode}")
	public ResponseEntity<List<Inventory>> findByProductCode(@PathVariable String productCode) {
		return ResponseEntity.ok(inventoryService.findByProductCode(productCode));
	}

	@GetMapping("/{productCode}/depositor/{depositorId}")
	public ResponseEntity<Inventory> findByProductCodeAndDepositor(
			@PathVariable String productCode,
			@PathVariable String depositorId) {
		Inventory inventory = inventoryService.findByProductCodeAndDepositor(productCode, depositorId);

		if (inventory == null) {
			return ResponseEntity.notFound().build();
		}

		return ResponseEntity.ok(inventory);
	}

	@PostMapping("/{productCode}/movements")
	public ResponseEntity<Void> add(
			@PathVariable String productCode,
			@RequestBody Depositor depositor,
			@RequestParam int quantity) {
		inventoryService.add(productCode, depositor, quantity);

		return ResponseEntity.noContent().build();
	}
}
