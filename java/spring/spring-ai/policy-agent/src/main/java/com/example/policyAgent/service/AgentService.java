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
	private final ChatClient chatClientPlanSynthesis;
	private final PlanService planService;

	AgentService(
			ChatService chatService,
			@Qualifier("chatClientPlanDecision") ChatClient chatClientPlanDecision,
			@Qualifier("chatClientPlanCreation") ChatClient chatClientPlanCreation,
			@Qualifier("chatClientPlanSynthesis") ChatClient chatClientPlanSynthesis,
			PlanService planService
	) {
		this.chatService = chatService;
		this.chatClientPlanDecision = chatClientPlanDecision;
		this.chatClientPlanCreation = chatClientPlanCreation;
		this.chatClientPlanSynthesis = chatClientPlanSynthesis;
		this.planService = planService;
	}

	public String call(ChatRequest chatRequest) {
		var planDecision = getPlanDecision(chatRequest.message());

		if (! planDecision.requiresPlan()) {
			return chatService.chat(chatRequest);
		}

		Plan plan = createPlan(chatRequest.message());

		return executePlan(plan);

	}

	private String executePlan(Plan plan) {

		plan.setStatus(Plan.Status.RUNNING);
		planService.save(plan);

		boolean failed = false;

		for (var task : plan.getTasks()) {
			if (failed) {
				task.setStatus(Plan.Status.SKIPPED);
				continue;
			}
			failed = ! planService.executeTask(plan, task);
		}

		plan.setStatus(failed ? Plan.Status.FAILED : Plan.Status.COMPLETED);
		planService.save(plan);

		return synthesize(plan);

	}

	private String synthesize(Plan plan) {
		StringBuilder prompt = new StringBuilder();
		prompt.append("Original user request:\n")
				.append(plan.getContext())
				.append("\n\nTask results:\n");

		for (var task : plan.getTasks()) {
			prompt.append("- ")
					.append(task.getObjective())
					.append(" [")
					.append(task.getStatus())
					.append("]: ")
					.append(task.getStatus() == Plan.Status.SKIPPED
							? "not executed because a previous task failed"
							: task.getResult())
					.append("\n");
		}

		return chatClientPlanSynthesis
				.prompt(prompt.toString())
				.call()
				.content();
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
