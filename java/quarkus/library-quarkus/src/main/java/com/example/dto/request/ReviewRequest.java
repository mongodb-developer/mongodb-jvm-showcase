package com.example.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;

public record ReviewRequest(
        @NotBlank(message = "bookId is required")
        String bookId,

        @NotBlank(message = "user is required")
        String user,

        @DecimalMin(value = "0.0", message = "rating must be at least 0.0")
        @DecimalMax(value = "5.0", message = "rating must be at most 5.0")
        double rating,

        @NotBlank(message = "text is required")
        String text
) {
}
