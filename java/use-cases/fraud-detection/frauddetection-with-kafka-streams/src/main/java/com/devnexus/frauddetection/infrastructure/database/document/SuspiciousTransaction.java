package com.devnexus.frauddetection.infrastructure.database.document;

import com.devnexus.frauddetection.domain.model.Transaction;
import com.devnexus.frauddetection.domain.model.VectorMatch;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Document(collection = "suspicious_transactions")
public record SuspiciousTransaction(
        @Id String id,
        Transaction transaction,
        DetectionType detectionType,
        String ruleId,
        String description,
        Double topScore,
        Double threshold,
        List<VectorMatch> matches,
        Instant detectedAt
) {
        public enum DetectionType { RULE, VECTOR }
}
