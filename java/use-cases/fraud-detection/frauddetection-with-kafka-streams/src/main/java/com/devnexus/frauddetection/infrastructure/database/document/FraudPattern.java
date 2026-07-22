package com.devnexus.frauddetection.infrastructure.database.document;

import com.devnexus.frauddetection.domain.model.Transaction;
import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Vector;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document("fraud_patterns")
public record FraudPattern(
        @Id String id,
        boolean fraud,
        Transaction transaction,
        String ruleId,
        String description,
        Instant detectedAt,
        Vector embedding
) {}