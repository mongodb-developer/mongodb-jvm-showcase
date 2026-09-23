package com.example.resource;

import com.example.dto.request.AuthorRequest;
import com.example.dto.response.AuthorResponse;
import com.example.dto.response.PageResponse;
import com.example.service.AuthorService;
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

@Path("/authors")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthorResource {

    private final AuthorService authorService;

    public AuthorResource(AuthorService authorService) {
        this.authorService = authorService;
    }

    @GET
    public PageResponse<AuthorResponse> findAll(
            @QueryParam("page") @DefaultValue("0") @Min(0) int page,
            @QueryParam("size") @DefaultValue("20") @Min(1) @Max(100) int size) {
        return authorService.findAll(page, size);
    }

    @GET
    @Path("/{id}")
    public AuthorResponse findById(@PathParam("id") String id) {
        return authorService.findById(id);
    }

    @GET
    @Path("/nationality/{nationality}")
    public List<AuthorResponse> findByNationality(@PathParam("nationality") String nationality) {
        return authorService.findByNationality(nationality);
    }

    @POST
    public Response create(@Valid AuthorRequest request, @Context UriInfo uriInfo) {
        AuthorResponse created = authorService.create(request);
        URI location = uriInfo.getAbsolutePathBuilder().path(created.id()).build();
        return Response.created(location).entity(created).build();
    }

    @PUT
    @Path("/{id}")
    public AuthorResponse update(@PathParam("id") String id, @Valid AuthorRequest request) {
        return authorService.update(id, request);
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") String id) {
        authorService.delete(id);
        return Response.noContent().build();
    }
}
