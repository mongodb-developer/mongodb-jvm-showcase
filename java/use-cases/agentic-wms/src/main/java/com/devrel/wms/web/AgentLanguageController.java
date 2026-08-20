package com.devrel.wms.web;

import com.devrel.wms.config.AgentLanguage;
import com.devrel.wms.config.AgentLanguageSettings;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/settings/language")
public class AgentLanguageController {

	private final AgentLanguageSettings agentLanguageSettings;

	AgentLanguageController(AgentLanguageSettings agentLanguageSettings) {
		this.agentLanguageSettings = agentLanguageSettings;
	}

	@GetMapping
	public ResponseEntity<AgentLanguage> current() {
		return ResponseEntity.ok(agentLanguageSettings.language());
	}

	@PostMapping("/{language}")
	public ResponseEntity<AgentLanguage> change(@PathVariable AgentLanguage language) {
		agentLanguageSettings.change(language);

		return ResponseEntity.ok(agentLanguageSettings.language());
	}
}
