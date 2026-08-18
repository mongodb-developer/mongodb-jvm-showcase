package com.example.policyAgent.service;

import com.example.policyAgent.model.Plan;
import com.example.policyAgent.model.TaskExecution;
import com.example.policyAgent.repository.PlanRepository;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class PlanService {

	private final PlanRepository planRepository;
	private final ChatClient chatClientPlanExecution;

	PlanService(
			PlanRepository planRepository,
			@Qualifier("chatClientPlanExecution") ChatClient chatClientPlanExecution
	) {
		this.planRepository = planRepository;
		this.chatClientPlanExecution = chatClientPlanExecution;
	}

	public Plan create(Plan plan, String conversationId, String userRequest) {
		plan.setConversationId(conversationId);
		plan.setUserRequest(userRequest);
		plan.setStatus(Plan.Status.CREATED);
		plan.getTasks().forEach(task -> task.setStatus(Plan.Status.CREATED));
		return planRepository.save(plan);
	}

	public Plan save(Plan plan) {
		return planRepository.save(plan);
	}

	public Optional<Plan> findWaitingInput(String conversationId) {
		return planRepository.findFirstByConversationIdAndStatus(conversationId, Plan.Status.WAITING_INPUT);
	}

	public Plan.Status executeTask(Plan plan, Plan.Task task) {
		task.setStatus(Plan.Status.RUNNING);
		planRepository.save(plan);

		TaskExecution execution;
		try {
			execution = chatClientPlanExecution
					.prompt()
					.system(system -> system.param("current_date", LocalDate.now()))
					.user(buildTaskPrompt(plan, task))
					.call()
					.entity(TaskExecution.class);
		} catch (Exception exception) {
			execution = new TaskExecution(false, false,
					"The task could not be executed: " + exception.getMessage());
		}

		task.setResult(execution.output());
		task.setStatus(execution.toStatus());
		planRepository.save(plan);

		return task.getStatus();
	}

	private String buildTaskPrompt(Plan plan, Plan.Task currentTask) {
		StringBuilder prompt = new StringBuilder();
		prompt.append("Original message from the user. Any question you ask "
						+ "must be written in the same language as this message:\n")
				.append(plan.getUserRequest())
				.append("\n\n");

		prompt.append("Overall goal of the plan:\n")
				.append(plan.getContext())
				.append("\n\n");

		prompt.append("Results of the previous tasks:\n");
		boolean hasPrevious = false;
		for (Plan.Task task : plan.getTasks()) {
			if (task == currentTask) {
				break;
			}
			if (task.getStatus() == Plan.Status.COMPLETED) {
				prompt.append("- ")
						.append(task.getObjective())
						.append(": ")
						.append(task.getResult())
						.append("\n");
				hasPrevious = true;
			}
		}
		if (!hasPrevious) {
			prompt.append("- none, this is the first task\n");
		}

		if (!plan.getUserInputs().isEmpty()) {
			prompt.append("\nInformation already provided by the user:\n");
			plan.getUserInputs().forEach(input -> prompt.append("- ").append(input).append("\n"));
		}

		prompt.append("\nTask to execute now:\n")
				.append(currentTask.getObjective());

		return prompt.toString();
	}
}