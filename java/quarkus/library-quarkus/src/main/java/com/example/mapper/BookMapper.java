package com.example.mapper;

import com.example.dto.request.BookRequest;
import com.example.dto.response.BookResponse;
import com.example.model.entity.Book;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

@ApplicationScoped
public class BookMapper {

    private final ReviewMapper reviewMapper;

    @Inject
    BookMapper(ReviewMapper reviewMapper) {
        this.reviewMapper = reviewMapper;
    }

    public Book toEntity(BookRequest request) {
        Book book = new Book();
        applyRequest(book, request);
        return book;
    }

    public void applyRequest(Book book, BookRequest request) {
        book.title = request.title();
        book.pages = request.pages();
        book.year = request.year();
        book.authors = request.authors();
    }

    public BookResponse toResponse(Book book) {
        return new BookResponse(
                book.id,
                book.title,
                book.pages,
                book.year,
                book.authors,
                reviewMapper.toResponseList(book.reviews)
        );
    }

    public List<BookResponse> toResponseList(List<Book> books) {
        return books.stream().map(this::toResponse).toList();
    }
}
