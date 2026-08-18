package com.example.policyAgent.service;

import com.example.policyAgent.model.ChatRequest;
import com.example.policyAgent.model.InputClassification;
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
	private final ChatClient chatClientInputClassification;
	private final PlanService planService;

	AgentService(
			ChatService chatService,
			@Qualifier("chatClientPlanDecision") ChatClient chatClientPlanDecision,
			@Qualifier("chatClientPlanCreation") ChatClient chatClientPlanCreation,
			@Qualifier("chatClientPlanSynthesis") ChatClient chatClientPlanSynthesis,
			@Qualifier("chatClientInputClassification") ChatClient chatClientInputClassification,
			PlanService planService
	) {
		this.chatClientInputClassification = chatClientInputClassification;
		this.chatService = chatService;
		this.chatClientPlanDecision = chatClientPlanDecision;
		this.chatClientPlanCreation = chatClientPlanCreation;
		this.chatClientPlanSynthesis = chatClientPlanSynthesis;
		this.planService = planService;
	}

	public String call(ChatRequest chatRequest) {
		var waitingPlan = planService.findWaitingInput(chatRequest.conversationId());

		if (waitingPlan.isPresent()) {
			Plan plan = waitingPlan.get();
			String pendingQuestion = pendingQuestion(plan);

			if (! answersPendingQuestion(pendingQuestion, chatRequest.message())) {
				return chatService.chat(chatRequest) + "\n\n" + pendingQuestion;
			}

			plan.addUserInput(chatRequest.message());
			return executePlan(plan);
		}

		var planDecision = getPlanDecision(chatRequest.message());

		if (! planDecision.requiresPlan()) {
			return chatService.chat(chatRequest);
		}

		Plan plan = createPlan(chatRequest.message(), chatRequest.conversationId());

		return executePlan(plan);

	}

	private String executePlan(Plan plan) {

		plan.setStatus(Plan.Status.RUNNING);
		planService.save(plan);

		Plan.Status outcome = Plan.Status.COMPLETED;

		for (var task : plan.getTasks()) {
			if (task.getStatus() == Plan.Status.COMPLETED) {
				continue;
			}
			if (outcome != Plan.Status.COMPLETED) {
				task.setStatus(Plan.Status.SKIPPED);
				continue;
			}
			outcome = planService.executeTask(plan, task);
		}

		plan.setStatus(outcome);
		planService.save(plan);

		if (outcome == Plan.Status.WAITING_INPUT) {
			return pendingQuestion(plan);
		}

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

	private Plan createPlan(String context, String conversationId) {
		Plan plan = chatClientPlanCreation.prompt(
				context
		).call().entity(Plan.class);

		return planService.create(plan, conversationId, context);
	}

	private String pendingQuestion(Plan plan) {
		return plan.getTasks().stream()
				.filter(task -> task.getStatus() == Plan.Status.WAITING_INPUT)
				.map(Plan.Task::getResult)
				.findFirst()
				.orElseThrow();
	}

	private boolean answersPendingQuestion(String pendingQuestion, String message) {
		var classification = chatClientInputClassification
				.prompt("""
						Question asked to the user:
						%s

						Message sent by the user:
						%s""".formatted(pendingQuestion, message))
				.call()
				.entity(InputClassification.class);

		return classification.answersPendingQuestion();
	}


}
