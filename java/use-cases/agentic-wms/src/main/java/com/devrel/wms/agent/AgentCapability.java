package com.devrel.wms.agent;

public record AgentCapability(
        String name,
        String description,
        Object tools
) {}
