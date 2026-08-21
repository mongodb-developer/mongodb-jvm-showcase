package com.devrel.wms.web;

import com.devrel.wms.domain.Depositor;
import com.devrel.wms.knowledge.DepositorKnowledgeEntry;
import com.devrel.wms.service.DepositorPolicyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/depositor-policies")
public class DepositorPolicyController {

	private final DepositorPolicyService depositorPolicyService;

	DepositorPolicyController(DepositorPolicyService depositorPolicyService) {
		this.depositorPolicyService = depositorPolicyService;
	}

	@GetMapping
	public ResponseEntity<List<DepositorKnowledgeEntry>> findAll() {
		return ResponseEntity.ok(depositorPolicyService.findAll());
	}

	@GetMapping("/depositors")
	public ResponseEntity<List<Depositor>> findDepositors() {
		return ResponseEntity.ok(depositorPolicyService.findDepositors());
	}

	@GetMapping("/{depositorId}")
	public ResponseEntity<List<DepositorKnowledgeEntry>> findByDepositorId(@PathVariable String depositorId) {
		return ResponseEntity.ok(depositorPolicyService.findByDepositorId(depositorId));
	}

	@PostMapping
	public ResponseEntity<DepositorKnowledgeEntry> save(@RequestBody DepositorKnowledgeEntry entry) {
		return ResponseEntity.ok(depositorPolicyService.save(entry));
	}

	@DeleteMapping("/{depositorId}/{key}")
	public ResponseEntity<Void> delete(@PathVariable String depositorId, @PathVariable String key) {
		depositorPolicyService.delete(depositorId, key);

		return ResponseEntity.noContent().build();
	}

	@GetMapping("/{depositorId}/preview")
	public ResponseEntity<List<String>> preview(
			@PathVariable String depositorId,
			@RequestParam String question) {
		return ResponseEntity.ok(depositorPolicyService.preview(depositorId, question));
	}
}
