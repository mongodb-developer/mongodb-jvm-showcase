package com.example.mapper;

import com.example.dto.request.AuthorRequest;
import com.example.dto.response.AuthorResponse;
import com.example.model.entity.Author;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class AuthorMapper {

    public Author toEntity(AuthorRequest request) {
        Author author = new Author();
        applyRequest(author, request);
        return author;
    }

    public void applyRequest(Author author, AuthorRequest request) {
        author.name = request.name();
        author.nationality = request.nationality();
        author.birthYear = request.birthYear();
    }

    public AuthorResponse toResponse(Author author) {
        return new AuthorResponse(author.id, author.name, author.nationality, author.birthYear);
    }

    public List<AuthorResponse> toResponseList(List<Author> authors) {
        return authors.stream().map(this::toResponse).toList();
    }
}
