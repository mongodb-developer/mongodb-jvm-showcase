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
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class AgentRunner {

	private final Logger logger = LoggerFactory.getLogger(AgentRunner.class);
	private final AgentRunService agentRunService;
	private final AgentLanguageSettings agentLanguageSettings;

	AgentRunner(AgentRunService agentRunService, AgentLanguageSettings agentLanguageSettings) {
		this.agentRunService = agentRunService;
		this.agentLanguageSettings = agentLanguageSettings;
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
			String capability = tasks.get(index).tool();
			LocalDateTime startedAt = LocalDateTime.now();

			try {
				String result = executeTask(definition, goal, description, capability, tasks);

				tasks.set(index, new AgentRun.AgentTask(
						description, AgentRun.TaskStatus.COMPLETED, capability, result, startedAt, LocalDateTime.now()));
			} catch (Exception exception) {
				logger.error("Agent run {} failed on task: {}", agentRun.id(), description, exception);

				tasks.set(index, new AgentRun.AgentTask(
						description, AgentRun.TaskStatus.FAILED, capability, exception.getMessage(), startedAt, LocalDateTime.now()));

				return agentRunService.save(finish(
						agentRun, tasks, AgentRun.Status.FAILED, "Failed on task: " + description));
			}

			agentRunService.save(withTasks(agentRun, tasks));
		}

		return agentRunService.save(finish(
				agentRun, tasks, AgentRun.Status.COMPLETED, summarize(definition, goal, tasks)));
	}

	private String summarize(AgentDefinition definition, String goal, List<AgentRun.AgentTask> tasks) {
		try {
			return definition.reporter()
					.prompt()
					.user("""
                %s

                These are the results of the executed tasks:
                %s

                Summarize the outcome of this execution.
                %s
                """.formatted(goal, previousResults(tasks), agentLanguageSettings.instruction()))
					.call()
					.content();
		} catch (Exception exception) {
			logger.error("Could not summarize agent run for goal: {}", goal, exception);

			return null;
		}
	}

	private List<AgentRun.AgentTask> plan(AgentDefinition definition, String goal) {
		List<PlannedTask> planned = definition.planner()
				.prompt()
				.user("""
                %s

                These are the capabilities available to execute the plan:
                %s

                Create the execution plan required to accomplish this goal.
                Assign to each task exactly one capability, using its name.
                %s
                """.formatted(goal, capabilityCatalog(definition), agentLanguageSettings.instruction()))
				.call()
				.entity(new ParameterizedTypeReference<List<PlannedTask>>() {});

		if (planned == null || planned.isEmpty()) {
			throw new IllegalStateException("Planner returned no task for goal: " + goal);
		}

		return planned.stream()
				.map(task -> new AgentRun.AgentTask(
						task.description(), AgentRun.TaskStatus.PENDING, task.capability(), null, null, null))
				.toList();
	}

	private String capabilityCatalog(AgentDefinition definition) {
		return definition.capabilities().stream()
				.map(capability -> "- " + capability.name() + ": " + capability.description())
				.collect(Collectors.joining("\n"));
	}

	private Object[] toolsOf(AgentDefinition definition, String capability) {
		return definition.capabilities().stream()
				.filter(candidate -> candidate.name().equalsIgnoreCase(capability))
				.map(AgentCapability::tools)
				.filter(Objects::nonNull)
				.flatMap(List::stream)
				.toArray();
	}

	private String executeTask(
			AgentDefinition definition,
			String goal,
			String description,
			String capability,
			List<AgentRun.AgentTask> tasks
	) {
		Object[] tools = toolsOf(definition, capability);

		if (tools.length == 0) {
			logger.warn("Task has no known capability: {}. Running without tools", capability);
		}

		return definition.executor()
				.prompt()
				.system(system -> system
						.param("current_date", LocalDateTime.now()))
				.tools(tools)
				.user("""
                %s

                Results of the previous tasks:
                %s

                Execute only the following task of the execution plan:
                %s

                Use the available tools autonomously.
                Do not execute any other task of the plan.
                Answer with a short result of this task only.
                %s
                """.formatted(goal, previousResults(tasks), description, agentLanguageSettings.instruction()))
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
