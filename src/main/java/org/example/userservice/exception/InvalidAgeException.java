package org.example.userservice.exception;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidAgeException extends ValidationException {
    public InvalidAgeException(Integer age) {
        super(String.format("Некорректный возраст \"%s\". Допустимый диапазон 1 до 100", age));
    }
}
