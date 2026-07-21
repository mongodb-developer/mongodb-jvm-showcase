 package com.samples.growingthings.web;

import com.samples.growingthings.model.GrowRecommendation;
import com.samples.growingthings.service.GrowingService;
import com.samples.growingthings.service.GrowingService.Question;
import com.samples.growingthings.service.IngestService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class ChatController {

	private final GrowingService growingService;
	private final IngestService ingestService;

	ChatController(GrowingService growingService, IngestService ingestService) {
		this.growingService = growingService;
		this.ingestService = ingestService;
	}

	@PostMapping("/ingest")
	public Map<String, Object> ingest() {
		return ingestService.ingest();
	}

	@PostMapping("/chat-rag")
	public GrowRecommendation chatRag(@RequestBody Question question) {
		return growingService.chat(question);
	}
}



