package com.devrel.wms.config;

import com.devrel.wms.tool.ReplenishmentTool;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
			
			Complete the task autonomously and stop only after you have either:
			
			* determined that no replenishment is necessary; or
			* created the required replenishment.
			
			Current date: {current_date}
			
			""";

	@Bean
	public ChatClient chatClient(
			OpenAiChatModel openAiChatModel,
			ReplenishmentTool replenishmentTool
	) {
		return ChatClient.builder(openAiChatModel)
				.defaultSystem(PROMPT_TEMPLATE)
				.defaultTools(replenishmentTool)
				.build();
	}

}
