package com.example.service;

import com.example.dto.request.AuthorRequest;
import com.example.dto.response.AuthorResponse;
import com.example.dto.response.PageResponse;
import com.example.exception.ResourceNotFoundException;
import com.example.mapper.AuthorMapper;
import com.example.model.entity.Author;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.bson.types.ObjectId;

import java.util.List;

/**
 * Business operations for authors.
 *
 * <p>{@link Author} uses the <strong>Active Record</strong> pattern, so data
 * access happens through static/instance methods on the entity itself rather
 * than an injected repository. The service still owns the workflow (mapping,
 * id assignment, not-found handling) and never touches HTTP types — showing that
 * Active Record and clean layering are not mutually exclusive.
 */
@ApplicationScoped
public class AuthorService {

    private static final String RESOURCE = "Author";

    private final AuthorMapper authorMapper;

    @Inject
    AuthorService(AuthorMapper authorMapper) {
        this.authorMapper = authorMapper;
    }

    public PageResponse<AuthorResponse> findAll(int page, int size) {
        // Active Record: paging is expressed directly on the entity via Panache.
        List<Author> authors = Author.findAll(Sort.by("name")).page(Page.of(page, size)).list();
        List<AuthorResponse> content = authorMapper.toResponseList(authors);
        return PageResponse.of(content, page, size, Author.count());
    }

    public AuthorResponse findById(String id) {
        return authorMapper.toResponse(getExisting(id));
    }

    public List<AuthorResponse> findByNationality(String nationality) {
        return authorMapper.toResponseList(Author.findByNationality(nationality));
    }

    public AuthorResponse create(AuthorRequest request) {
        Author author = authorMapper.toEntity(request);
        author.id = new ObjectId().toHexString();
        author.persist();
        return authorMapper.toResponse(author);
    }

    public AuthorResponse update(String id, AuthorRequest request) {
        Author existing = getExisting(id);
        authorMapper.applyRequest(existing, request);
        existing.persistOrUpdate();
        return authorMapper.toResponse(existing);
    }

    public void delete(String id) {
        if (!Author.deleteById(id)) {
            throw ResourceNotFoundException.of(RESOURCE, id);
        }
    }

    private Author getExisting(String id) {
        Author author = Author.findById(id);
        if (author == null) {
            throw ResourceNotFoundException.of(RESOURCE, id);
        }
        return author;
    }
}
