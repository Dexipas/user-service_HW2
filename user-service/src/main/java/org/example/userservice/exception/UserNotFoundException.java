package org.example.userservice.exception;

import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class UserNotFoundException extends ValidationException {
    public UserNotFoundException(UUID id) {
        super(String.format("Пользователь с id \"%s\" не найден", id));
    }
    public UserNotFoundException(String email) {
        super(String.format("Пользователь с email \"%s\" не найден", email));
    }

}
