package com.devnexus.frauddetection.infrastructure.database;

import com.devnexus.frauddetection.domain.model.ScoringResult;
import com.devnexus.frauddetection.domain.model.SuspiciousAlert;
import com.devnexus.frauddetection.domain.model.Transaction;
import com.devnexus.frauddetection.domain.model.VectorMatch;
import com.devnexus.frauddetection.domain.port.TransactionPersistencePort;
import com.devnexus.frauddetection.infrastructure.database.document.ApprovedTransaction;
import com.devnexus.frauddetection.infrastructure.database.document.SuspiciousTransaction;
import com.devnexus.frauddetection.infrastructure.database.repository.ApprovedTransactionRepository;
import com.devnexus.frauddetection.infrastructure.database.repository.SuspiciousTransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@Component
public class MongoTransactionPersistenceAdapter implements TransactionPersistencePort {

    private static final Logger log = LoggerFactory.getLogger(MongoTransactionPersistenceAdapter.class);
    private final ApprovedTransactionRepository approvedRepo;
    private final SuspiciousTransactionRepository suspiciousRepo;

    public MongoTransactionPersistenceAdapter(
            ApprovedTransactionRepository approvedRepo,
            SuspiciousTransactionRepository suspiciousRepo
    ) {
        this.approvedRepo = approvedRepo;
        this.suspiciousRepo = suspiciousRepo;
    }

    @Override
    public void saveApproved(Transaction transaction) {
        approvedRepo.save(new ApprovedTransaction(null, transaction, Instant.now()));
    }

    @Override
    public void saveSuspiciousFromVector(Transaction transaction, ScoringResult scoringResult, double threshold) {
        List<VectorMatch> matches = scoringResult.matches();

        SuspiciousTransaction doc = new SuspiciousTransaction(
                null,
                transaction,
                SuspiciousTransaction.DetectionType.VECTOR,
                null,
                "Similar to known fraud patterns",
                scoringResult.topScore(),
                threshold,
                matches,
                Instant.now()
        );

        suspiciousRepo.save(doc);

        log.info("Suspicious transaction from vector similarity saved.");
    }

    @Override
    public void saveSuspiciousFromRule(SuspiciousAlert alert) {
        SuspiciousTransaction doc = new SuspiciousTransaction(
                null,
                alert.transaction(),
                SuspiciousTransaction.DetectionType.RULE,
                alert.ruleId(),
                alert.description(),
                null,
                null,
                null,
                alert.detectedAt() != null ? alert.detectedAt() : Instant.now()
        );

        suspiciousRepo.save(doc);

        log.info("Suspicious transaction from rules saved.");
    }
}
