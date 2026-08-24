package com.devrel.wms.limits;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("wms.limits")
public record DemoLimits(
		boolean enabled,
		int perIpRequestsPerMinute,
		int globalRequestsPerDay,
		int agentRunsPerDay,
		int maxPolicyTextLength) {

	public DemoLimits {
		perIpRequestsPerMinute = orDefault(perIpRequestsPerMinute, 10);
		globalRequestsPerDay = orDefault(globalRequestsPerDay, 500);
		agentRunsPerDay = orDefault(agentRunsPerDay, 50);
		maxPolicyTextLength = orDefault(maxPolicyTextLength, 2_000);
	}

	private static int orDefault(int value, int fallback) {
		return value <= 0 ? fallback : value;
	}
}
