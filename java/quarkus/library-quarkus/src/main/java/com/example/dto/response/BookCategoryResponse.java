package com.example.dto.response;

import java.util.List;

public record BookCategoryResponse(
        String id,
        String title,
        int pages,
        int year,
        List<String> authors,
        String pageCategory
) {
}
