package com.example.exception;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public static ResourceNotFoundException of(String resource, String id) {
        return new ResourceNotFoundException("%s not found: %s".formatted(resource, id));
    }
}
