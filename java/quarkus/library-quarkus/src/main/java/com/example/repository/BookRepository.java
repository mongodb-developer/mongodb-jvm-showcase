package com.example.repository;

import com.example.dto.response.AuthorBookCountResponse;
import com.example.dto.response.BookCategoryResponse;
import com.example.dto.response.BookWithReviewsResponse;
import com.example.dto.response.PageResponse;
import com.example.dto.response.ReviewResponse;
import com.example.model.entity.Book;
import com.example.model.entity.Review;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Facet;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.PushOptions;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.model.Updates;
import io.quarkus.mongodb.panache.PanacheMongoRepositoryBase;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Function;

@ApplicationScoped
public class BookRepository implements PanacheMongoRepositoryBase<Book, String> {

    private static final String BOOKS_COLLECTION = "books";
    private static final String REVIEWS_COLLECTION = "reviews";

    private final MongoCollection<Document> books;
    private final int maxEmbeddedReviews;

    @Inject
    BookRepository(MongoDatabase database,
                   @ConfigProperty(name = "library.reviews.max-embedded", defaultValue = "5")
                   int maxEmbeddedReviews) {
        this.books = database.getCollection(BOOKS_COLLECTION);
        this.maxEmbeddedReviews = maxEmbeddedReviews;
    }

    public List<Book> findPage(int page, int size) {
        return findAll(Sort.by("title")).page(page, size).list();
    }

    public List<Book> findByPagesGreaterThan(int minPages) {
        return find("pages > ?1", minPages).list();
    }

    public List<Book> findByYear(int year) {
        return list("year", year);
    }

    public List<Book> findSortedByYear(boolean ascending) {
        Sort.Direction direction = ascending ? Sort.Direction.Ascending : Sort.Direction.Descending;
        return listAll(Sort.by("year", direction));
    }

    public List<Book> findLongest(int limit) {
        return findAll(Sort.by("pages", Sort.Direction.Descending)).page(0, limit).list();
    }

    public void update(Book book) {
        persistOrUpdate(book);
    }

    public PageResponse<BookCategoryResponse> classifyByPageCount(int page, int size) {
        Document setStage = new Document("$set", new Document("pageCategory",
                new Document("$switch", new Document()
                        .append("branches", List.of(
                                new Document("case", new Document("$lte", List.of("$pages", 250))).append("then", "short"),
                                new Document("case", new Document("$lte", List.of("$pages", 500))).append("then", "medium")
                        ))
                        .append("default", "long"))));

        return paginate(List.of(setStage), page, size, doc -> new BookCategoryResponse(
                idOf(doc),
                doc.getString("title"),
                doc.getInteger("pages", 0),
                doc.getInteger("year", 0),
                authorsOf(doc),
                doc.getString("pageCategory")));
    }

    public PageResponse<AuthorBookCountResponse> countBooksPerAuthor(int page, int size) {
        List<Bson> stages = List.of(
                Aggregates.unwind("$authors"),
                Aggregates.group("$authors", Accumulators.sum("totalBooks", 1)),
                Aggregates.sort(Sorts.descending("totalBooks")));

        return paginate(stages, page, size, doc -> new AuthorBookCountResponse(
                doc.getString("_id"),
                doc.getInteger("totalBooks", 0)));
    }

    public PageResponse<BookWithReviewsResponse> findBooksWithReviews(int page, int size) {
        List<Bson> stages = List.of(
                Aggregates.lookup(REVIEWS_COLLECTION, "_id", "bookId", "allReviews"),
                Aggregates.match(Filters.not(Filters.size("allReviews", 0))));

        return paginate(stages, page, size, doc -> new BookWithReviewsResponse(
                idOf(doc),
                doc.getString("title"),
                doc.getInteger("pages", 0),
                doc.getInteger("year", 0),
                authorsOf(doc),
                toReviewResponses(doc.getList("allReviews", Document.class))));
    }

    /**
     * Paginates an aggregation server-side with a single {@code $facet} stage:
     * the {@code data} branch applies {@code $skip}/{@code $limit} for the page,
     * the {@code count} branch computes the total — one round trip, no second
     * count query.
     */
    private <T> PageResponse<T> paginate(List<Bson> stages, int page, int size,
                                         Function<Document, T> mapper) {
        Facet dataFacet = new Facet("data", Aggregates.skip(page * size), Aggregates.limit(size));
        Facet countFacet = new Facet("count", Aggregates.count("total"));

        List<Bson> pipeline = new ArrayList<>(stages);
        pipeline.add(Aggregates.facet(dataFacet, countFacet));

        Document result = books.aggregate(pipeline).first();
        if (result == null) {
            return PageResponse.of(List.of(), page, size, 0);
        }

        List<Document> data = result.getList("data", Document.class, List.of());
        List<Document> countDocs = result.getList("count", Document.class, List.of());
        long total = countDocs.isEmpty() ? 0 : ((Number) countDocs.get(0).get("total")).longValue();

        List<T> content = data.stream().map(mapper).toList();
        return PageResponse.of(content, page, size, total);
    }

    public void embedReview(Review review) {
        Document embedded = new Document()
                .append("_id", review.id)
                .append("text", review.text)
                .append("user", review.user)
                .append("rating", review.rating)
                .append("createdAt", Date.from(review.createdAt));

        books.updateOne(
                Filters.eq("_id", review.bookId),
                Updates.pushEach("reviews", List.of(embedded),
                        new PushOptions()
                                .sortDocument(new Document("createdAt", 1))
                                .slice(-maxEmbeddedReviews)));
    }

    private static String idOf(Document doc) {
        Object id = doc.get("_id");
        return id != null ? id.toString() : null;
    }

    @SuppressWarnings("unchecked")
    private static List<String> authorsOf(Document doc) {
        return doc.getList("authors", String.class, List.of());
    }

    private static List<ReviewResponse> toReviewResponses(List<Document> reviewDocs) {
        if (reviewDocs == null) {
            return List.of();
        }
        return reviewDocs.stream().map(BookRepository::toReviewResponse).toList();
    }

    private static ReviewResponse toReviewResponse(Document doc) {
        Date createdAt = doc.getDate("createdAt");
        double rating = doc.get("rating") instanceof Number number ? number.doubleValue() : 0.0;
        return new ReviewResponse(
                idOf(doc),
                doc.getString("bookId"),
                doc.getString("user"),
                rating,
                doc.getString("text"),
                createdAt != null ? createdAt.toInstant() : null);
    }
}
