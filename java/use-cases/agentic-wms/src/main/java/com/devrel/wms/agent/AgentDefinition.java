package com.devrel.wms.agent;

import org.springframework.ai.chat.client.ChatClient;

public record AgentDefinition(
        String trigger,
        String goal,
        ChatClient planner,
        ChatClient executor,
        ChatClient reporter
) {}
