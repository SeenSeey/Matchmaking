package com.example.matcmaking_api.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String resource, Object id) {
        super(String.format("%s с ID %s не найден", resource, id));
    }
}

