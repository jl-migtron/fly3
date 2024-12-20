package com.example.fly3.exceptions;

public class ResourceNotFoundException extends RuntimeException {

    private static final String DEFAULT_MESSAGE = "Entity is not found";

    public ResourceNotFoundException() {
        super(DEFAULT_MESSAGE);
    }

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
