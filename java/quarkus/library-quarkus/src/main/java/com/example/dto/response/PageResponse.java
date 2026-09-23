package com.example.dto.response;

import java.util.List;

/**
 * Generic pagination envelope returned by listing endpoints. Carries the page
 * content plus the metadata a client needs to navigate (current page, size,
 * totals, and first/last flags). Page indexing is zero-based.
 *
 * @param <T> the type of the items in {@link #content}
 */
public record PageResponse<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean first,
        boolean last
) {

    /**
     * Builds a page response, deriving {@code totalPages} and the first/last
     * flags from the total number of elements.
     */
    public static <T> PageResponse<T> of(List<T> content, int page, int size, long totalElements) {
        int totalPages = size > 0 ? (int) Math.ceil((double) totalElements / size) : 0;
        boolean first = page == 0;
        boolean last = page >= totalPages - 1;
        return new PageResponse<>(content, page, size, totalElements, totalPages, first, last);
    }
}
