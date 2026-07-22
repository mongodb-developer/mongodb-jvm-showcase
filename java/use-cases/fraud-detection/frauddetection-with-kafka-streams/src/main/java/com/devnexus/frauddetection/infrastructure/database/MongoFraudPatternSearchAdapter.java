package com.devnexus.frauddetection.infrastructure.database;

import com.devnexus.frauddetection.domain.model.ScoringResult;
import com.devnexus.frauddetection.domain.model.VectorMatch;
import com.devnexus.frauddetection.domain.port.FraudPatternSearchPort;
import com.devnexus.frauddetection.infrastructure.database.document.FraudPattern;
import com.devnexus.frauddetection.infrastructure.database.repository.FraudPatternRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Score;
import org.springframework.data.domain.SearchResult;
import org.springframework.data.domain.SearchResults;
import org.springframework.data.domain.Vector;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MongoFraudPatternSearchAdapter implements FraudPatternSearchPort {

    private static final Logger logger = LoggerFactory.getLogger(MongoFraudPatternSearchAdapter.class);

    private final FraudPatternRepository fraudPatternRepository;

    public MongoFraudPatternSearchAdapter(FraudPatternRepository fraudPatternRepository) {
        this.fraudPatternRepository = fraudPatternRepository;
    }

    @Override
    public ScoringResult searchSimilarPatterns(Vector embedding, double threshold) {

        SearchResults<FraudPattern> results =
                fraudPatternRepository.searchTopFraudPatternsByEmbeddingNear(
                        embedding,
                        Score.of(threshold)
                );

        List<SearchResult<FraudPattern>> content = results.getContent();

        printLog(content);

        if (content.isEmpty()) {
            return ScoringResult.noMatch();
        }

        double topScore = content.getFirst().getScore().getValue();

        long fraudCount = content.stream()
                .filter(r -> r.getContent().fraud())
                .count();

        // notFraud
        long safeCount = content.size() - fraudCount;

        boolean classifiedFraud = fraudCount >= safeCount;

        if (classifiedFraud) {
            List<VectorMatch> matches = content.stream()
                    .map(r -> new VectorMatch(r.getContent().id(), r.getScore().getValue()))
                    .toList();

            return ScoringResult.withMatches(topScore, matches);
        }

        return ScoringResult.noMatch();

    }

	private void printLog(List<SearchResult<FraudPattern>> content) {
        int i = 0;

        for (SearchResult<FraudPattern> r : content) {
            FraudPattern fp = r.getContent();
            double score = r.getScore() != null ? r.getScore().getValue() : 0.0;

            String matchId = fp != null ? fp.id() : "<null>";
            boolean matchFraud = fp != null && fp.fraud();
            String matchRule = fp != null ? fp.ruleId() : "<null>";
            String matchDesc = fp != null ? fp.description() : "<null>";
            String matchMerchant = (fp != null && fp.transaction() != null) ? fp.transaction().merchant() : "<null>";
            String matchCity = (fp != null && fp.transaction() != null) ? fp.transaction().city() : "<null>";
            String matchTxId = (fp != null && fp.transaction() != null) ? fp.transaction().transactionId() : "<null>";

            logger.info(">>> MATCH[{}]: score={}, fraud={}, id={}, ruleId={}, txId={}, merchant={}, city={}, desc={}",
                    i++, score, matchFraud, matchId, matchRule, matchTxId, matchMerchant, matchCity, matchDesc);
        }
	}
}
