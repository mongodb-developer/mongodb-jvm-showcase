package com.example.dto.response;

import java.util.List;

public record BookResponse(
        String id,
        String title,
        int pages,
        int year,
        List<String> authors,
        List<ReviewResponse> reviews
) {
}
