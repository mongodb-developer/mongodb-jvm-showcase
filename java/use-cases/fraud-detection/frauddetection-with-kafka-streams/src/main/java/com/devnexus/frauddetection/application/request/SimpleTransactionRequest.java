package com.devnexus.frauddetection.application.request;

import com.devnexus.frauddetection.domain.model.Transaction;

import java.math.BigDecimal;
import java.time.Instant;

public record SimpleTransactionRequest(
		String transactionId,
		BigDecimal amount
) {
	public Transaction toTransaction() {
		return new Transaction(
				transactionId, null, null, null, amount, Instant.now(), null, null, null
		);
	}
}