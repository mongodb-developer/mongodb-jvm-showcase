package com.example.policyAgent.service;

import com.example.policyAgent.model.ChatRequest;
import com.example.policyAgent.model.Plan;
import com.example.policyAgent.model.PlanDecision;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class AgentService {

	private final ChatService chatService;
	private final ChatClient chatClientPlanDecision;
	private final ChatClient chatClientPlanCreation;
	private final PlanService planService;

	AgentService(
			ChatService chatService,
			@Qualifier("chatClientPlanDecision") ChatClient chatClientPlanDecision,
			@Qualifier("chatClientPlanCreation") ChatClient chatClientPlanCreation,
			PlanService planService
	) {
		this.chatService = chatService;
		this.chatClientPlanDecision = chatClientPlanDecision;
		this.chatClientPlanCreation = chatClientPlanCreation;
		this.planService = planService;
	}

	public String call(ChatRequest chatRequest) {
		var planDecision = getPlanDecision(chatRequest.message());

		if (! planDecision.requiresPlan()) {
			return chatService.chat(chatRequest);
		}

		Plan plan = createPlan(planDecision.reason());

		var result = executePlan(plan);

		return result;

	}

	private String executePlan(Plan plan) {

		for (var task : plan.getTasks()) {
			planService.executeTask(task);
		}

		return "test";

	}

	private PlanDecision getPlanDecision(String message) {
		return chatClientPlanDecision
				.prompt(message)
				.call()
				.entity(PlanDecision.class);
	}

	private Plan createPlan(String context) {
		Plan plan = chatClientPlanCreation.prompt(
				context
		).call().entity(Plan.class);

		return planService.create(plan);
	}


}
