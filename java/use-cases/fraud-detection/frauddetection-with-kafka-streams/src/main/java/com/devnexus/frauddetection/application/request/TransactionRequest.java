package com.devnexus.frauddetection.application.request;

import com.devnexus.frauddetection.domain.model.Transaction;

import java.math.BigDecimal;
import java.time.Instant;

public record TransactionRequest (
		String transactionId,
		String userId,
		String merchant,
		String city,
		BigDecimal transactionAmount,
		Instant transactionTime,
		String cardNumber,
		Double latitude,
		Double longitude
) {
	public Transaction toTransaction(){
		return new Transaction(
				transactionId, userId, merchant, city, transactionAmount, transactionTime, cardNumber, latitude, longitude
		);
	}
}