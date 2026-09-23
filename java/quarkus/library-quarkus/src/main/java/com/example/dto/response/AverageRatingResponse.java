package com.example.dto.response;

public record AverageRatingResponse(
        String bookId,
        double averageRating,
        int totalReviews
) {
}
