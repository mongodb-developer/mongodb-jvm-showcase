package com.devrel.wms.agent;

import com.devrel.wms.domain.AgentRun;
import com.devrel.wms.service.AgentRunService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AgentRunner {

	private final Logger logger = LoggerFactory.getLogger(AgentRunner.class);
	private final AgentRunService agentRunService;

	AgentRunner(AgentRunService agentRunService) {
		this.agentRunService = agentRunService;
	}

	public AgentRun run(AgentDefinition definition, String reference) {
		String goal = definition.goal().formatted(reference);
		List<AgentRun.AgentTask> tasks = new ArrayList<>(plan(definition, goal));

		AgentRun agentRun = agentRunService.save(new AgentRun(
				null,
				definition.trigger(),
				reference,
				AgentRun.Status.RUNNING,
				null,
				LocalDateTime.now(),
				null,
				List.copyOf(tasks)
		));

		logger.info("Agent run {} started with {} task(s)", agentRun.id(), tasks.size());

		for (int index = 0; index < tasks.size(); index++) {
			String description = tasks.get(index).description();
			LocalDateTime startedAt = LocalDateTime.now();

			try {
				String result = executeTask(definition, goal, description, tasks);

				tasks.set(index, new AgentRun.AgentTask(
						description, AgentRun.TaskStatus.COMPLETED, null, result, startedAt, LocalDateTime.now()));
			} catch (Exception exception) {
				logger.error("Agent run {} failed on task: {}", agentRun.id(), description, exception);

				tasks.set(index, new AgentRun.AgentTask(
						description, AgentRun.TaskStatus.FAILED, null, exception.getMessage(), startedAt, LocalDateTime.now()));

				return agentRunService.save(finish(
						agentRun, tasks, AgentRun.Status.FAILED, "Failed on task: " + description));
			}

			agentRunService.save(withTasks(agentRun, tasks));
		}

		return agentRunService.save(finish(
				agentRun, tasks, AgentRun.Status.COMPLETED, tasks.getLast().result()));
	}

	private List<AgentRun.AgentTask> plan(AgentDefinition definition, String goal) {
		List<String> descriptions = definition.planner()
				.prompt()
				.user("""
                %s

                Create the execution plan required to accomplish this goal.
                """.formatted(goal))
				.call()
				.entity(new ParameterizedTypeReference<List<String>>() {});

		if (descriptions == null || descriptions.isEmpty()) {
			throw new IllegalStateException("Planner returned no task for goal: " + goal);
		}

		return descriptions.stream()
				.map(description -> new AgentRun.AgentTask(
						description, AgentRun.TaskStatus.PENDING, null, null, null, null))
				.toList();
	}

	private String executeTask(
			AgentDefinition definition,
			String goal,
			String description,
			List<AgentRun.AgentTask> tasks
	) {
		return definition.executor()
				.prompt()
				.system(system -> system
						.param("current_date", LocalDateTime.now()))
				.user("""
                %s

                Results of the previous tasks:
                %s

                Execute only the following task of the execution plan:
                %s

                Use the available tools autonomously.
                Do not execute any other task of the plan.
                Answer with a short result of this task only.
                """.formatted(goal, previousResults(tasks), description))
				.call()
				.content();
	}

	private String previousResults(List<AgentRun.AgentTask> tasks) {
		String results = tasks.stream()
				.filter(task -> task.status() == AgentRun.TaskStatus.COMPLETED)
				.map(task -> "- " + task.description() + ": " + task.result())
				.collect(Collectors.joining("\n"));

		return results.isBlank() ? "None" : results;
	}

	private AgentRun withTasks(AgentRun agentRun, List<AgentRun.AgentTask> tasks) {
		return new AgentRun(
				agentRun.id(),
				agentRun.trigger(),
				agentRun.reference(),
				agentRun.status(),
				agentRun.summary(),
				agentRun.startedAt(),
				agentRun.completedAt(),
				List.copyOf(tasks)
		);
	}

	private AgentRun finish(
			AgentRun agentRun,
			List<AgentRun.AgentTask> tasks,
			AgentRun.Status status,
			String summary
	) {
		return new AgentRun(
				agentRun.id(),
				agentRun.trigger(),
				agentRun.reference(),
				status,
				summary,
				agentRun.startedAt(),
				LocalDateTime.now(),
				List.copyOf(tasks)
		);
	}
}
