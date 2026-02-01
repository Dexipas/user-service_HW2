package org.example.userservice.exception;

public class UserNotFoundException extends ValidationException {
    public UserNotFoundException(Long id) {
        super(String.format("Пользователь с id \"%s\" не найден", id));
    }
}
