package com.devrel.wms.agent;

import java.util.List;

public record AgentCapability(
        String name,
        String description,
        List<Object> tools
) {}
