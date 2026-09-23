package com.example.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record AuthorRequest(
        @NotBlank(message = "name is required")
        String name,

        @NotBlank(message = "nationality is required")
        String nationality,

        @Min(value = 0, message = "birthYear must not be negative")
        @Max(value = 2100, message = "birthYear is not a valid year")
        int birthYear
) {
}
