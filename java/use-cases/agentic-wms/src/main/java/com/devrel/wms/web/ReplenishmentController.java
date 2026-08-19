package com.devrel.wms.web;

import com.devrel.wms.entity.Replenishment;
import com.devrel.wms.service.ReplenishmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/replenishment")
public class ReplenishmentController {

	private final ReplenishmentService replenishmentService;

	ReplenishmentController(ReplenishmentService replenishmentService) {
		this.replenishmentService = replenishmentService;
	}

	@PostMapping
	public ResponseEntity<Replenishment> create(@RequestBody Replenishment replenishment) {
		return ResponseEntity.ok(replenishmentService.save(replenishment));
	}

	@GetMapping("/{id}")
	public ResponseEntity<Replenishment> findById(@PathVariable String id) {
		return ResponseEntity.ok(replenishmentService.findById(id));
	}
}
