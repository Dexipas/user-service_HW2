package org.example.userservice.exception;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class AgeNullException extends ValidationException{
    public AgeNullException(Integer age) {
        super(String.format("Возраст \"%s\" равен null", age));
    }
}
