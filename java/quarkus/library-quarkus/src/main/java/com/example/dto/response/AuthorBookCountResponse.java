package com.example.dto.response;

public record AuthorBookCountResponse(
        String author,
        int totalBooks
) {
}
