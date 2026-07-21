package com.example.langchain4j.controller;

import com.example.langchain4j.service.Assistant;
import com.example.langchain4j.service.IngestionService;
import com.example.langchain4j.service.SearchService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class RagController {

    private final Assistant assistant;
    private final IngestionService ingestionService;
    private final SearchService searchService;

    @PostMapping("/ingest")
    public Map<String, Object> ingest() throws Exception {
        int count = ingestionService.ingest();
        return Map.of("ingested", count);
    }
    public RagController(Assistant assistant,
                         IngestionService ingestionService,
                         SearchService searchService) {
        this.assistant = assistant;
        this.ingestionService = ingestionService;
        this.searchService = searchService;
    }

    public record ChatRequest(String question) {}
    public record ChatResponse(String answer) {}

    @PostMapping("/search")
    public List<SearchService.Match> search(@RequestBody ChatRequest request) {
        return searchService.search(request.question());
    }

    @PostMapping("/chat")
    public ChatResponse chat(@RequestBody ChatRequest request) {
        return new ChatResponse(assistant.answer(request.question()));
    }
}
