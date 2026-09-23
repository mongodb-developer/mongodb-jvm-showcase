package com.example.exception;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.util.List;

@Provider
public class ConstraintViolationExceptionMapper implements ExceptionMapper<ConstraintViolationException> {

    @Override
    public Response toResponse(ConstraintViolationException exception) {
        List<String> details = exception.getConstraintViolations().stream()
                .map(this::formatViolation)
                .sorted()
                .toList();

        ApiError error = ApiError.of(
                Response.Status.BAD_REQUEST.getStatusCode(),
                "Bad Request",
                "Request validation failed",
                details
        );
        return Response.status(Response.Status.BAD_REQUEST).entity(error).build();
    }

    private String formatViolation(ConstraintViolation<?> violation) {
        String path = violation.getPropertyPath().toString();
        int lastDot = path.lastIndexOf('.');
        String field = lastDot >= 0 ? path.substring(lastDot + 1) : path;
        return "%s: %s".formatted(field, violation.getMessage());
    }
}
