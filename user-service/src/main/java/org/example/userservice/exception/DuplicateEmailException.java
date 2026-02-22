package org.example.userservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class DuplicateEmailException extends ValidationException{

    public DuplicateEmailException(String email) {
        super(String.format("Такой Email \"%s\" уже есть в системе", email));
    }
}
