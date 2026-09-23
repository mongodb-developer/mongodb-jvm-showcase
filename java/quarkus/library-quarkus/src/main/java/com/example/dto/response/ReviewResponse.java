package com.example.dto.response;

import java.time.Instant;

public record ReviewResponse(
        String id,
        String bookId,
        String user,
        double rating,
        String text,
        Instant createdAt
) {
}
