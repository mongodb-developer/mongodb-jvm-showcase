package com.example.resource;

import com.example.dto.request.BookRequest;
import com.example.dto.response.AuthorBookCountResponse;
import com.example.dto.response.BookCategoryResponse;
import com.example.dto.response.BookResponse;
import com.example.dto.response.BookWithReviewsResponse;
import com.example.dto.response.PageResponse;
import com.example.service.BookService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import java.net.URI;
import java.util.List;

@Path("/books")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class BookResource {

    private final BookService bookService;

    public BookResource(BookService bookService) {
        this.bookService = bookService;
    }

    @GET
    public PageResponse<BookResponse> findAll(
            @QueryParam("page") @DefaultValue("0") @Min(0) int page,
            @QueryParam("size") @DefaultValue("20") @Min(1) @Max(100) int size) {
        return bookService.findAll(page, size);
    }

    @GET
    @Path("/{id}")
    public BookResponse findById(@PathParam("id") String id) {
        return bookService.findById(id);
    }

    @GET
    @Path("/min-pages/{minPages}")
    public List<BookResponse> findByMinPages(@PathParam("minPages") int minPages) {
        return bookService.findByMinPages(minPages);
    }

    @GET
    @Path("/year/{year}")
    public List<BookResponse> findByYear(@PathParam("year") int year) {
        return bookService.findByYear(year);
    }

    @GET
    @Path("/sort/year")
    public List<BookResponse> sortedByYear(@QueryParam("order") @DefaultValue("desc") String order) {
        return bookService.sortedByYear(order);
    }

    @GET
    @Path("/top/{limit}")
    public List<BookResponse> longestBooks(@PathParam("limit") int limit) {
        return bookService.longestBooks(limit);
    }

    @GET
    @Path("/classify")
    public PageResponse<BookCategoryResponse> classifyByPageCount(
            @QueryParam("page") @DefaultValue("0") @Min(0) int page,
            @QueryParam("size") @DefaultValue("20") @Min(1) @Max(100) int size) {
        return bookService.classifyByPageCount(page, size);
    }

    @GET
    @Path("/per-author")
    public PageResponse<AuthorBookCountResponse> countBooksPerAuthor(
            @QueryParam("page") @DefaultValue("0") @Min(0) int page,
            @QueryParam("size") @DefaultValue("20") @Min(1) @Max(100) int size) {
        return bookService.countBooksPerAuthor(page, size);
    }

    @GET
    @Path("/with-reviews")
    public PageResponse<BookWithReviewsResponse> booksWithReviews(
            @QueryParam("page") @DefaultValue("0") @Min(0) int page,
            @QueryParam("size") @DefaultValue("20") @Min(1) @Max(100) int size) {
        return bookService.booksWithReviews(page, size);
    }

    @POST
    public Response create(@Valid BookRequest request, @Context UriInfo uriInfo) {
        BookResponse created = bookService.create(request);
        URI location = uriInfo.getAbsolutePathBuilder().path(created.id()).build();
        return Response.created(location).entity(created).build();
    }

    @PUT
    @Path("/{id}")
    public BookResponse update(@PathParam("id") String id, @Valid BookRequest request) {
        return bookService.update(id, request);
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") String id) {
        bookService.delete(id);
        return Response.noContent().build();
    }
}
