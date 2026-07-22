package com.devnexus.frauddetection.infrastructure.embedding.seed;

import com.devnexus.frauddetection.infrastructure.database.document.FraudPattern;
import com.devnexus.frauddetection.infrastructure.database.repository.FraudPatternRepository;
import com.devnexus.frauddetection.infrastructure.embedding.voyage.VoyageTransactionEmbedderAdapter;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FraudSeededEmbeddingBackfill implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(FraudSeededEmbeddingBackfill.class);

    private final FraudPatternRepository repo;
    private final VoyageTransactionEmbedderAdapter embedder;

    public FraudSeededEmbeddingBackfill(FraudPatternRepository repo, VoyageTransactionEmbedderAdapter embedder) {
        this.repo = repo;
        this.embedder = embedder;
    }

    @Override
    public void run(String @NonNull ... args) {
        List<FraudPattern> missing = repo.findAll().stream()
                .filter(d -> d.embedding() == null)
                .toList();

        if (missing.isEmpty()) {
            log.info("Seeded fraud docs already have embeddings. Nothing to backfill.");
            return;
        }

        log.info("Backfilling embeddings for {} seeded fraud docs...", missing.size());

        for (var d : missing) {
            var tx = d.transaction();
            var vector = embedder.embed(tx);

            FraudPattern updated = new FraudPattern(
                    d.id(),
                    d.fraud(),
                    d.transaction(),
                    d.ruleId(),
                    d.description(),
                    d.detectedAt(),
                    vector
            );

            repo.save(updated);
            log.info("Embedded seeded fraud doc id={} ruleId={} dims={}", d.id(), d.ruleId(), vector.size());
        }

        log.info("Done.");
    }
}
