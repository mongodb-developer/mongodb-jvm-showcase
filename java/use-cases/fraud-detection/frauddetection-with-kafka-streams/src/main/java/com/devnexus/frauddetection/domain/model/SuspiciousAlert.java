package com.devnexus.frauddetection.domain.model;

import java.time.Instant;

public record SuspiciousAlert(
		Transaction transaction,
		String ruleId,
		String description,
		Instant detectedAt
) {
	public static SuspiciousAlert of(Transaction transaction, String ruleId, String description) {
		return new SuspiciousAlert(transaction, ruleId, description, Instant.now());
	}
}
