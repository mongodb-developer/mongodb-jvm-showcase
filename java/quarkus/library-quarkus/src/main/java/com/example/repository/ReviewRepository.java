package com.example.repository;

import com.example.dto.response.AverageRatingResponse;
import com.example.model.entity.Review;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import io.quarkus.mongodb.panache.PanacheMongoRepositoryBase;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class ReviewRepository implements PanacheMongoRepositoryBase<Review, String> {

    private static final String REVIEWS_COLLECTION = "reviews";

    private final MongoCollection<Document> reviews;

    @Inject
    ReviewRepository(MongoDatabase database) {
        this.reviews = database.getCollection(REVIEWS_COLLECTION);
    }

    /** Returns a single page of reviews, most recent first. */
    public List<Review> findPage(int page, int size) {
        return findAll(Sort.by("createdAt", Sort.Direction.Descending)).page(page, size).list();
    }

    public void update(Review review) {
        persistOrUpdate(review);
    }

    public Optional<AverageRatingResponse> averageRatingForBook(String bookId) {
        List<Bson> pipeline = List.of(
                Aggregates.match(Filters.eq("bookId", bookId)),
                Aggregates.group(
                        "$bookId",
                        Accumulators.avg("averageRating", "$rating"),
                        Accumulators.sum("totalReviews", 1)));

        Document result = reviews.aggregate(pipeline).first();
        if (result == null) {
            return Optional.empty();
        }

        double averageRating = result.get("averageRating") instanceof Number number ? number.doubleValue() : 0.0;
        return Optional.of(new AverageRatingResponse(
                result.getString("_id"),
                averageRating,
                result.getInteger("totalReviews", 0)));
    }
}
