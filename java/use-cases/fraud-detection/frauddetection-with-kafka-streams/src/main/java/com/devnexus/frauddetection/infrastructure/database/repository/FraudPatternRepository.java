package com.devnexus.frauddetection.infrastructure.database.repository;

import com.devnexus.frauddetection.infrastructure.database.document.FraudPattern;
import org.springframework.data.domain.Score;
import org.springframework.data.domain.SearchResults;
import org.springframework.data.domain.Vector;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.VectorSearch;
import org.springframework.stereotype.Repository;

@Repository
public interface FraudPatternRepository extends MongoRepository<FraudPattern, String> {

    @VectorSearch(
            indexName = "fraud_patterns_vector_index",
            limit = "10",
            numCandidates = "200"
    )
    SearchResults<FraudPattern> searchTopFraudPatternsByEmbeddingNear(
            Vector vector,
            Score score
    );
}

