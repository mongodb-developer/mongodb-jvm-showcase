package com.devnexus.frauddetection.domain.model;

import java.math.BigDecimal;
import java.time.Instant;

public record Transaction(
		String transactionId,
		String userId,
		String merchant,
		String city,
		BigDecimal transactionAmount,
		Instant transactionTime,
		String cardNumber,
		Double latitude,
		Double longitude
) {}