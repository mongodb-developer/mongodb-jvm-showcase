package com.example.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;

import java.util.List;

public record BookRequest(
        @NotBlank(message = "title is required")
        String title,

        @Positive(message = "pages must be greater than zero")
        int pages,

        @Min(value = 0, message = "year must not be negative")
        @Max(value = 2100, message = "year is not a valid year")
        int year,

        @NotEmpty(message = "at least one author is required")
        List<@NotBlank(message = "author name must not be blank") String> authors
) {
}
