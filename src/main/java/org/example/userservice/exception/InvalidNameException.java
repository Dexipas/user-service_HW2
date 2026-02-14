package org.example.userservice.exception;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidNameException extends ValidationException{
    public InvalidNameException(String name) {
        super(String.format("Некорректное имя \"%s\". Допустимы только буквы. Имя должно начинаться с заглавной буквы",name));
    }
}
