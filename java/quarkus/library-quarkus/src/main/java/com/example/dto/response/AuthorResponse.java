package com.example.dto.response;

public record AuthorResponse(
        String id,
        String name,
        String nationality,
        int birthYear
) {
}
