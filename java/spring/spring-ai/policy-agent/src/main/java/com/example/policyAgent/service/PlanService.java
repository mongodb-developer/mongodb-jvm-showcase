package com.example.policyAgent.service;

import com.example.policyAgent.model.Plan;
import com.example.policyAgent.model.TaskExecution;
import com.example.policyAgent.repository.PlanRepository;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

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

	public Plan create(Plan plan) {
		plan.setStatus(Plan.Status.CREATED);
		plan.getTasks().forEach(task -> task.setStatus(Plan.Status.CREATED));
		return planRepository.save(plan);
	}

	public Plan save(Plan plan) {
		return planRepository.save(plan);
	}

	public Plan findById(String id) {
		return planRepository.findById(id).orElseThrow();
	}

	public boolean executeTask(Plan plan, Plan.Task task) {
		task.setStatus(Plan.Status.RUNNING);
		planRepository.save(plan);

		TaskExecution execution;
		try {
			execution = chatClientPlanExecution
					.prompt(buildTaskPrompt(plan, task))
					.call()
					.entity(TaskExecution.class);
		} catch (Exception exception) {
			execution = new TaskExecution(false, "The task could not be executed: " + exception.getMessage());
		}

		task.setResult(execution.output());
		task.setStatus(execution.success() ? Plan.Status.COMPLETED : Plan.Status.FAILED);
		planRepository.save(plan);

		return execution.success();
	}

	private String buildTaskPrompt(Plan plan, Plan.Task currentTask) {
		StringBuilder prompt = new StringBuilder();
		prompt.append("Overall goal of the plan:\n")
				.append(plan.getContext())
				.append("\n\n");

		prompt.append("Results of the previous tasks:\n");
		boolean hasPrevious = false;
		for (Plan.Task task : plan.getTasks()) {
			if (task == currentTask) {
				break;
			}
			if (task.getResult() != null) {
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

		prompt.append("\nTask to execute now:\n")
				.append(currentTask.getObjective());

		return prompt.toString();
	}
}