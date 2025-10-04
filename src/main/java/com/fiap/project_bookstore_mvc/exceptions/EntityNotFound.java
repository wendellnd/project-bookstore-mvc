package com.fiap.project_bookstore_mvc.exceptions;

public class EntityNotFound extends RuntimeException {

    public EntityNotFound(String message) {
        super(message);
    }
}