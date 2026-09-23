package com.example.service;

import com.example.dto.request.BookRequest;
import com.example.dto.response.AuthorBookCountResponse;
import com.example.dto.response.BookCategoryResponse;
import com.example.dto.response.BookResponse;
import com.example.dto.response.BookWithReviewsResponse;
import com.example.dto.response.PageResponse;
import com.example.exception.ResourceNotFoundException;
import com.example.mapper.BookMapper;
import com.example.model.entity.Book;
import com.example.repository.BookRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.bson.types.ObjectId;

import java.util.List;

@ApplicationScoped
public class BookService {

    private static final String RESOURCE = "Book";

    private final BookRepository bookRepository;
    private final BookMapper bookMapper;

    @Inject
    BookService(BookRepository bookRepository, BookMapper bookMapper) {
        this.bookRepository = bookRepository;
        this.bookMapper = bookMapper;
    }

    public PageResponse<BookResponse> findAll(int page, int size) {
        List<BookResponse> content = bookMapper.toResponseList(bookRepository.findPage(page, size));
        return PageResponse.of(content, page, size, bookRepository.count());
    }

    public BookResponse findById(String id) {
        return bookMapper.toResponse(getExisting(id));
    }

    public List<BookResponse> findByMinPages(int minPages) {
        return bookMapper.toResponseList(bookRepository.findByPagesGreaterThan(minPages));
    }

    public List<BookResponse> findByYear(int year) {
        return bookMapper.toResponseList(bookRepository.findByYear(year));
    }

    public List<BookResponse> sortedByYear(String order) {
        boolean ascending = "asc".equalsIgnoreCase(order);
        return bookMapper.toResponseList(bookRepository.findSortedByYear(ascending));
    }

    public List<BookResponse> longestBooks(int limit) {
        return bookMapper.toResponseList(bookRepository.findLongest(limit));
    }

    public BookResponse create(BookRequest request) {
        Book book = bookMapper.toEntity(request);
        book.id = new ObjectId().toHexString();
        bookRepository.persist(book);
        return bookMapper.toResponse(book);
    }

    public BookResponse update(String id, BookRequest request) {
        Book existing = getExisting(id);
        bookMapper.applyRequest(existing, request);
        bookRepository.update(existing);
        return bookMapper.toResponse(existing);
    }

    public void delete(String id) {
        if (!bookRepository.deleteById(id)) {
            throw ResourceNotFoundException.of(RESOURCE, id);
        }
    }

    public PageResponse<BookCategoryResponse> classifyByPageCount(int page, int size) {
        return bookRepository.classifyByPageCount(page, size);
    }

    public PageResponse<AuthorBookCountResponse> countBooksPerAuthor(int page, int size) {
        return bookRepository.countBooksPerAuthor(page, size);
    }

    public PageResponse<BookWithReviewsResponse> booksWithReviews(int page, int size) {
        return bookRepository.findBooksWithReviews(page, size);
    }

    private Book getExisting(String id) {
        return bookRepository.findByIdOptional(id)
                .orElseThrow(() -> ResourceNotFoundException.of(RESOURCE, id));
    }
}
