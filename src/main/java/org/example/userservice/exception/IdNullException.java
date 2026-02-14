package org.example.userservice.exception;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class IdNullException extends ValidationException {
    public IdNullException(String id) {
        super(String.format("Id \"%s\" равен null", id));
    }
}
