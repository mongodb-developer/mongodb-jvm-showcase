package com.devrel.wms.web;

import com.devrel.wms.entity.StockMovement;
import com.devrel.wms.service.StockMovementService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/stock-movements")
public class StockMovementController {

	private final StockMovementService stockMovementService;

	StockMovementController(StockMovementService stockMovementService) {
		this.stockMovementService = stockMovementService;
	}

	@GetMapping
	public ResponseEntity<List<StockMovement>> findAll() {
		return ResponseEntity.ok(stockMovementService.findAll());
	}

	@GetMapping("/product/{productCode}")
	public ResponseEntity<List<StockMovement>> findByProductCode(@PathVariable String productCode) {
		return ResponseEntity.ok(stockMovementService.findByProductCode(productCode));
	}

	@GetMapping("/invoice/{invoiceNumber}")
	public ResponseEntity<List<StockMovement>> findByInvoiceNumber(@PathVariable String invoiceNumber) {
		return ResponseEntity.ok(stockMovementService.findByInvoiceNumber(invoiceNumber));
	}
}
