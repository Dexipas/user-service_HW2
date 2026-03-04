package org.example.userservice.exception;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class EmailEmptyOrNullException extends ValidationException{

    public EmailEmptyOrNullException(String email) {
        super(String.format("Email \"%s\" пустой или равен null", email));
    }
}
