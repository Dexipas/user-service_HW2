package org.example.userservice.exception;

public class EmailEmptyOrNullException extends ValidationException{

    public EmailEmptyOrNullException(String email) {
        super(String.format("Email \"%s\" пустой или равен null", email));
    }
}
