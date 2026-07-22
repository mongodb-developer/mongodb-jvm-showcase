package com.devnexus.frauddetection.infrastructure.database.document;

import com.devnexus.frauddetection.domain.model.Transaction;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document("approved_transactions")
public record ApprovedTransaction(
        @Id String id,
        Transaction transaction,
        Instant approvedAt
) {}
