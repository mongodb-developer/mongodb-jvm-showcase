package com.devnexus.frauddetection.application.usecase;

import com.devnexus.frauddetection.domain.model.SuspiciousAlert;
import com.devnexus.frauddetection.domain.port.TransactionPersistencePort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SaveSuspiciousTransactionUseCase {

	private static final Logger log = LoggerFactory.getLogger(SaveSuspiciousTransactionUseCase.class);

	private final TransactionPersistencePort persistence;

	public SaveSuspiciousTransactionUseCase(TransactionPersistencePort persistence) {
		this.persistence = persistence;
	}

	public void execute(SuspiciousAlert alert) {
		if (alert == null || alert.transaction() == null) {
			log.warn(">>> Suspicious alert: <null>");
			return;
		}

		var tx = alert.transaction();

		log.warn(">>> SUSPICIOUS (RULE): txId={}, userId={}, ruleId={}, desc={}",
				tx.transactionId(),
				tx.userId(),
				alert.ruleId(),
				alert.description()
		);

		persistence.saveSuspiciousFromRule(alert);
	}
}
