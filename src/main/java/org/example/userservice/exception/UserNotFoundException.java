package org.example.userservice.exception;

import java.util.UUID;

public class UserNotFoundException extends ValidationException {
    public UserNotFoundException(UUID id) {
        super(String.format("Пользователь с id \"%s\" не найден", id));
    }
}
