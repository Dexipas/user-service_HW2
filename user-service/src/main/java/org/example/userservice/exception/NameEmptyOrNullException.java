package org.example.userservice.exception;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class NameEmptyOrNullException extends ValidationException{
    public NameEmptyOrNullException(String name) {
        super(String.format("Имя \"%s\" пустое или равно null", name));
    }
}
