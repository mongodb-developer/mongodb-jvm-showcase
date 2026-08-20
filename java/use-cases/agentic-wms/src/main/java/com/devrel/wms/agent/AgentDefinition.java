package com.devrel.wms.agent;

import org.springframework.ai.chat.client.ChatClient;

import java.util.List;

public record AgentDefinition(
        String trigger,
        String goal,
        ChatClient planner,
        ChatClient executor,
        ChatClient reporter,
        List<AgentCapability> capabilities
) {}
