package com.devnexus.frauddetection.domain.port;

import com.devnexus.frauddetection.domain.model.ScoringResult;
import com.devnexus.frauddetection.domain.model.SuspiciousAlert;
import com.devnexus.frauddetection.domain.model.Transaction;

public interface TransactionPersistencePort {
    void saveApproved(Transaction transaction);
    void saveSuspiciousFromVector(Transaction transaction, ScoringResult scoringResult, double threshold);
    void saveSuspiciousFromRule(SuspiciousAlert alert);
}
