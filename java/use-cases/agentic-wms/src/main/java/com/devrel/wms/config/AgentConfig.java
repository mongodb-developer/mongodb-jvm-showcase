package com.devrel.wms.config;

import com.devrel.wms.agent.AgentCapability;
import com.devrel.wms.agent.AgentDefinition;
import com.devrel.wms.tool.DepositorEmailTool;
import com.devrel.wms.tool.InventoryAnalysisTool;
import com.devrel.wms.tool.ReplenishmentTool;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class AgentConfig {

	private static final String PROMPT_TEMPLATE = """
			You are an AI agent specialized in Warehouse Management Systems (WMS).
			
			Your goal is to analyze inventory after each outbound operation and anticipate replenishment needs.
			
			Use the available tools to:
			
			* check current inventory;
			* inspect recent stock movements;
			* analyze recent outbound quantities and dates.
			
			Operate autonomously. Do not ask the user for information that can be obtained through tools.
			
			When an outbound invoice is completed, use its invoice number to identify the affected products and perform the replenishment analysis.
			
			Analyze current stock, recent consumption, and outbound frequency to determine whether a product may run out soon.
			
			Do not create a replenishment just because an outbound operation occurred. Avoid unnecessary or duplicate replenishments.
			
			Create a `replenishment` only when there is clear evidence that a product is out of stock or likely to run out soon.
			
			When creating a replenishment:
			
			* include only the products that need replenishment;
			* choose a reasonable quantity based on current stock and recent consumption;
			* include a short message explaining the reason.
			
			If inventory is healthy, take no action.

			The notification email is only drafted, never sent by you: it is sent when a warehouse
			operator approves the request. Never state that the email was sent.

			When you use the drafting tool, reproduce in your answer the full email content
			returned by the tool, exactly as it is, without summarizing or rewriting it.

			You always receive a single task of an execution plan. Execute only that task.
			Never perform an action that belongs to another task, even when it looks like the
			natural next step.

			Complete the task autonomously and stop as soon as it is done.
			
			Current date: {current_date}
			
			""";

	private static final String PROMPT_PLAN_CREATION_TEMPLATE = """
			You are the planning agent for a Warehouse Management System (WMS).
			
			Your responsibility is to create a short execution plan for another AI agent to follow.
			
			Based on the event and context provided, create only the tasks required to accomplish the goal.
			
			Rules:
			
			* Do not execute any task.
			* Do not call tools.
			* Do not make business decisions.
			* Create tasks in the correct execution order.
			* Keep tasks short, clear, and observable.
			* Avoid unnecessary tasks.
			* Use conditional tasks when the action depends on information that will only be discovered during execution.
			* Do not expose internal reasoning or chain of thought.
			* Create a maximum of 6 tasks.
			* Always filter by depositor.
			* Assign exactly one capability to each task, using the capability name.
			* A task must never mix two capabilities. Creating a replenishment request and
			  writing the notification email are always two separate tasks, in this order.

			For replenishment analysis after an outbound invoice, a typical plan may include:

			1. Identify the products affected by the outbound invoice. [ANALYSIS]
			2. Check the current inventory for the affected products for the specific depositor. [ANALYSIS]
			3. Analyze recent stock movements and consumption for the specific depositor. [ANALYSIS]
			4. Determine whether replenishment is required. [DECISION]
			5. Create a replenishment request if necessary. [REPLENISHMENT]
			6. Write the notification email for the depositor if a replenishment request was created. [NOTIFICATION]

			Return only the execution plan.

	""";

	private static final String PROMPT_SUMMARY_TEMPLATE = """
			You are the reporting agent for a Warehouse Management System (WMS).

			Write a short summary explaining the outcome of an agent execution.

			Rules:

			* Explain what was decided and why it was decided.
			* When a replenishment was requested, justify it with the evidence found:
			  current stock on hand, recent outbound frequency and consumption.
			* When no replenishment was necessary, explain why the inventory is healthy.
			* Do not include email content, greetings, signatures or recipient addresses.
			* Do not repeat the task list.
			* Maximum of three sentences.
			* Plain text only.

			""";

	@Bean
	public ChatClient chatClient(OpenAiChatModel openAiChatModel) {
		return ChatClient.builder(openAiChatModel)
				.defaultSystem(PROMPT_TEMPLATE)
				.build();
	}

	@Bean("chatClientPlanCreation")
	public ChatClient chatClientPlanCreation(OpenAiChatModel openAiChatModel) {
		return ChatClient
				.builder(openAiChatModel)
				.defaultSystem(PROMPT_PLAN_CREATION_TEMPLATE)
				.build();
	}

	@Bean("chatClientSummary")
	public ChatClient chatClientSummary(OpenAiChatModel openAiChatModel) {
		return ChatClient
				.builder(openAiChatModel)
				.defaultSystem(PROMPT_SUMMARY_TEMPLATE)
				.build();
	}

	@Bean
	public AgentDefinition replenishmentAgent(
			ChatClient chatClient,
			@Qualifier("chatClientPlanCreation") ChatClient chatClientPlanCreation,
			@Qualifier("chatClientSummary") ChatClient chatClientSummary,
			InventoryAnalysisTool inventoryAnalysisTool,
			ReplenishmentTool replenishmentTool,
			DepositorEmailTool depositorEmailTool
	) {
		return new AgentDefinition(
				"OUTBOUND_INVOICE_COMPLETED",
				"Outbound invoice %s has just been completed.",
				chatClientPlanCreation,
				chatClient,
				chatClientSummary,
				List.of(
						new AgentCapability(
								"ANALYSIS",
								"Read inventory, stock movements and invoice movements. Cannot change anything.",
								inventoryAnalysisTool),
						new AgentCapability(
								"REPLENISHMENT",
								"Create a replenishment request. Cannot write the notification email.",
								replenishmentTool),
						new AgentCapability(
								"NOTIFICATION",
								"Write the notification email of an existing replenishment request.",
								depositorEmailTool),
						new AgentCapability(
								"DECISION",
								"Decide, based on the results of previous tasks, whether replenishment is required. Uses no tool.",
								null)
				)
		);
	}
}
