package com.example.resource;

import com.example.dto.request.ReviewRequest;
import com.example.dto.response.AverageRatingResponse;
import com.example.dto.response.PageResponse;
import com.example.dto.response.ReviewResponse;
import com.example.service.ReviewService;
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

@Path("/reviews")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ReviewResource {

    private final ReviewService reviewService;

    public ReviewResource(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GET
    public PageResponse<ReviewResponse> findAll(
            @QueryParam("page") @DefaultValue("0") @Min(0) int page,
            @QueryParam("size") @DefaultValue("20") @Min(1) @Max(100) int size) {
        return reviewService.findAll(page, size);
    }

    @GET
    @Path("/avgRating/{bookId}")
    public AverageRatingResponse getAverageBookRating(@PathParam("bookId") String bookId) {
        return reviewService.getAverageBookRating(bookId);
    }

    @POST
    public Response create(@Valid ReviewRequest request, @Context UriInfo uriInfo) {
        ReviewResponse created = reviewService.create(request);
        URI location = uriInfo.getAbsolutePathBuilder().path(created.id()).build();
        return Response.created(location).entity(created).build();
    }

    @PUT
    @Path("/{id}")
    public ReviewResponse update(@PathParam("id") String id, @Valid ReviewRequest request) {
        return reviewService.update(id, request);
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") String id) {
        reviewService.delete(id);
        return Response.noContent().build();
    }
}
