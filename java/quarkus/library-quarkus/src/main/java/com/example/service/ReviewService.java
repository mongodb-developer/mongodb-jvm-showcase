package com.example.service;

import com.example.dto.request.ReviewRequest;
import com.example.dto.response.AverageRatingResponse;
import com.example.dto.response.PageResponse;
import com.example.dto.response.ReviewResponse;
import com.example.exception.ResourceNotFoundException;
import com.example.mapper.ReviewMapper;
import com.example.model.entity.Review;
import com.example.repository.BookRepository;
import com.example.repository.ReviewRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.bson.types.ObjectId;

import java.time.Instant;
import java.util.List;

@ApplicationScoped
public class ReviewService {

    private static final String RESOURCE = "Review";

    private final ReviewRepository reviewRepository;
    private final BookRepository bookRepository;
    private final ReviewMapper reviewMapper;

    @Inject
    ReviewService(ReviewRepository reviewRepository,
                  BookRepository bookRepository,
                  ReviewMapper reviewMapper) {
        this.reviewRepository = reviewRepository;
        this.bookRepository = bookRepository;
        this.reviewMapper = reviewMapper;
    }

    public PageResponse<ReviewResponse> findAll(int page, int size) {
        List<ReviewResponse> content = reviewMapper.toResponseList(reviewRepository.findPage(page, size));
        return PageResponse.of(content, page, size, reviewRepository.count());
    }

    public AverageRatingResponse getAverageBookRating(String bookId) {
        return reviewRepository.averageRatingForBook(bookId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No reviews found for book: " + bookId));
    }

    public ReviewResponse create(ReviewRequest request) {
        Review review = reviewMapper.toEntity(request);
        review.id = new ObjectId().toHexString();
        review.createdAt = Instant.now();

        reviewRepository.persist(review);
        bookRepository.embedReview(review);
        return reviewMapper.toResponse(review);
    }

    public ReviewResponse update(String id, ReviewRequest request) {
        Review existing = reviewRepository.findByIdOptional(id)
                .orElseThrow(() -> ResourceNotFoundException.of(RESOURCE, id));
        reviewMapper.applyRequest(existing, request);
        reviewRepository.update(existing);
        return reviewMapper.toResponse(existing);
    }

    public void delete(String id) {
        if (!reviewRepository.deleteById(id)) {
            throw ResourceNotFoundException.of(RESOURCE, id);
        }
    }
}
