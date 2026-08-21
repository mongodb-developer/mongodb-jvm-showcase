package com.devrel.wms.web;

import com.devrel.wms.domain.Depositor;
import com.devrel.wms.service.DepositorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/depositors")
public class DepositorController {

	private final DepositorService depositorService;

	DepositorController(DepositorService depositorService) {
		this.depositorService = depositorService;
	}

	@PostMapping
	public ResponseEntity<Depositor> create(@RequestBody Depositor depositor) {
		return ResponseEntity.ok(depositorService.save(depositor));
	}

	@GetMapping
	public ResponseEntity<List<Depositor>> findAll() {
		return ResponseEntity.ok(depositorService.findAll());
	}

	@GetMapping("/{id}")
	public ResponseEntity<Depositor> findById(@PathVariable String id) {
		return ResponseEntity.ok(depositorService.findById(id));
	}

	@PutMapping("/{id}")
	public ResponseEntity<Depositor> update(@PathVariable String id,
			@RequestBody Depositor depositor) {
		return ResponseEntity.ok(depositorService.update(id, depositor));
	}
}
