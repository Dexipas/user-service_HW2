package org.example.userservice.exception;

public class IdNullException extends ValidationException {
    public IdNullException(Long id) {
        super(String.format("Id \"%s\" равен null", id));
    }
}
