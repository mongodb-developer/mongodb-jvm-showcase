package com.example.exception;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class ResourceNotFoundExceptionMapper implements ExceptionMapper<ResourceNotFoundException> {

    @Override
    public Response toResponse(ResourceNotFoundException exception) {
        ApiError error = ApiError.of(
                Response.Status.NOT_FOUND.getStatusCode(),
                "Not Found",
                exception.getMessage()
        );
        return Response.status(Response.Status.NOT_FOUND).entity(error).build();
    }
}
