package com.devrel.wms.web;

import com.devrel.wms.domain.AgentRun;
import com.devrel.wms.service.AgentRunService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/agent-runs")
public class AgentRunController {

	private final AgentRunService agentRunService;

	AgentRunController(AgentRunService agentRunService) {
		this.agentRunService = agentRunService;
	}

	@GetMapping
	public ResponseEntity<List<AgentRun>> findAll() {
		return ResponseEntity.ok(agentRunService.findAll());
	}

	@GetMapping("/{id}")
	public ResponseEntity<AgentRun> findById(@PathVariable String id) {
		AgentRun agentRun = agentRunService.findById(id);

		if (agentRun == null) {
			return ResponseEntity.notFound().build();
		}

		return ResponseEntity.ok(agentRun);
	}
}
