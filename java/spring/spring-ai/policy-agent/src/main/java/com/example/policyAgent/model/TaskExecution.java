package com.example.policyAgent.model;

public record TaskExecution(boolean success, boolean needsUserInput, String output) {

	public Plan.Status toStatus() {
		if (success) {
			return Plan.Status.COMPLETED;
		}
		return needsUserInput ? Plan.Status.WAITING_INPUT : Plan.Status.FAILED;
	}
}