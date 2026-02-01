package org.example.userservice.exception;

public class InvalidEmailException extends ValidationException {

    public InvalidEmailException(String email) {
        super(String.format("Некорректный email \"%s\". Пример: primer_123@mailbox.ru", email));

    }
}
