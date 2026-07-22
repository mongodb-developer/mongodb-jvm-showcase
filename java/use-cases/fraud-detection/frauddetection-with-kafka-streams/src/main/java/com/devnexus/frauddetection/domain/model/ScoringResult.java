package com.devnexus.frauddetection.domain.model;

import java.util.List;

public record ScoringResult(boolean matchFound, double topScore, List<VectorMatch> matches) {

    public static ScoringResult noMatch() {
        return new ScoringResult(false, 0.0, List.of());
    }

    public static ScoringResult withMatches(double topScore, List<VectorMatch> matches) {
        return new ScoringResult(true, topScore, matches);
    }
}
