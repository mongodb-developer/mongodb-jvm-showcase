package com.devnexus.frauddetection.infrastructure.embedding.voyage;

import com.devnexus.frauddetection.domain.model.Transaction;

import java.math.RoundingMode;

public final class TransactionEmbeddingText {

    private TransactionEmbeddingText() {}

    public static String toText(Transaction tx) {

        String amount = tx.transactionAmount() == null
                ? "unknown"
                : tx.transactionAmount()
                .setScale(2, RoundingMode.HALF_UP)
                .toPlainString();

        return """
                merchant=%s,
                city=%s,
                amount=%s,
                time=%s,
                latitude=%s,
                longitude=%s
                """.formatted(
                safe(tx.merchant()),
                safe(tx.city()),
                amount,
                tx.transactionTime(),
                tx.latitude(),
                tx.longitude()
        );
    }

    private static String safe(String s) {
        return (s == null || s.isBlank()) ? "unknown" : s.trim();
    }
}
