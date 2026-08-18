package com.devrel.wms.web;

import com.devrel.wms.entity.Inventory;
import com.devrel.wms.service.InventoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/inventory")
public class InventoryController {

	private final InventoryService inventoryService;

	InventoryController(InventoryService inventoryService) {
		this.inventoryService = inventoryService;
	}

	@PostMapping
	public ResponseEntity<Inventory> create(@RequestBody Inventory inventory) {
		return ResponseEntity.ok(inventoryService.save(inventory));
	}

	@GetMapping("/add/{productCode}/{quantity}")
	public ResponseEntity<String> add(@PathVariable String productCode, @PathVariable int quantity) {
		return ResponseEntity.ok(inventoryService.add(productCode, quantity));
	}
}
