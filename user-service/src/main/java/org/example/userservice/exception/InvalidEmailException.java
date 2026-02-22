package org.example.userservice.exception;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidEmailException extends ValidationException {

    public InvalidEmailException(String email) {
        super(String.format("Некорректный email \"%s\". Пример: primer_123@mailbox.ru", email));

    }
}
