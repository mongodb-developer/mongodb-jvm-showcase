package com.example.dto.response;

import java.util.List;

public record BookWithReviewsResponse(
        String id,
        String title,
        int pages,
        int year,
        List<String> authors,
        List<ReviewResponse> reviews
) {
}
