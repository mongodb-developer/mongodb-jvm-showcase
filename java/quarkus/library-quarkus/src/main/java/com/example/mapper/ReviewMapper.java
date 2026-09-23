package com.example.mapper;

import com.example.dto.request.ReviewRequest;
import com.example.dto.response.ReviewResponse;
import com.example.model.entity.Review;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class ReviewMapper {

    public Review toEntity(ReviewRequest request) {
        Review review = new Review();
        review.bookId = request.bookId();
        applyRequest(review, request);
        return review;
    }

    public void applyRequest(Review review, ReviewRequest request) {
        review.user = request.user();
        review.rating = request.rating();
        review.text = request.text();
    }

    public ReviewResponse toResponse(Review review) {
        return new ReviewResponse(
                review.id,
                review.bookId,
                review.user,
                review.rating,
                review.text,
                review.createdAt
        );
    }

    public List<ReviewResponse> toResponseList(List<Review> reviews) {
        if (reviews == null) {
            return List.of();
        }
        return reviews.stream().map(this::toResponse).toList();
    }
}
