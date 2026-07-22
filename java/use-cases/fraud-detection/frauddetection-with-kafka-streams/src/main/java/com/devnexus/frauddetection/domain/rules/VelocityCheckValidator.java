package com.devnexus.frauddetection.domain.rules;

import com.devnexus.frauddetection.domain.model.SuspiciousAlert;
import com.devnexus.frauddetection.domain.model.Transaction;

import java.util.Optional;

public class VelocityCheckValidator {

    private static final String RULE_ID = "VELOCITY_CHECK";

    private final long maxTransactions;
    private final long windowMinutes;

    public VelocityCheckValidator(long maxTransactions, long windowMinutes) {
        this.maxTransactions = maxTransactions;
        this.windowMinutes = windowMinutes;
    }

    public long getWindowMinutes() {
        return windowMinutes;
    }

    public Optional<SuspiciousAlert> validate(long transactionCount, Transaction transaction) {
        if (transactionCount <= maxTransactions) {
            return Optional.empty();
        }

        String description = String.format(
                "%d transactions in %d minute window (max: %d)",
                transactionCount,
                windowMinutes,
                maxTransactions
        );

        return Optional.of(SuspiciousAlert.of(transaction, RULE_ID, description));
    }
}