package com.example.policyAgent.web;

import com.example.policyAgent.model.ChatRequest;
import com.example.policyAgent.model.ConversationHistory;
import com.example.policyAgent.model.ConversationListItem;
import com.example.policyAgent.service.AgentService;
import com.example.policyAgent.service.ChatService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class PolicyController {

	private final ChatService chatService;
	private final AgentService agentService;

	public PolicyController(ChatService chatService, AgentService agentService) {
		this.chatService = chatService;
		this.agentService = agentService;
	}

	@PostMapping("/chat")
	public String chat(@RequestBody ChatRequest chatRequest) {

		return agentService.call(chatRequest);
	}

	@GetMapping("/chat")
	public List<ConversationListItem> conversations() {
		return chatService.conversations();
	}

	@GetMapping("/chat/{conversationId}")
	public ConversationHistory history(@PathVariable String conversationId) {
		return chatService.history(conversationId);
	}

	@DeleteMapping("/chat/{conversationId}")
	public void delete(@PathVariable String conversationId) {
		chatService.delete(conversationId);
	}
}
