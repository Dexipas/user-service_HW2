package org.example.userservice.exception;

public class DuplicateEmailException extends ValidationException{

    public DuplicateEmailException(String email) {
        super(String.format("Такой Email \"%s\" уже есть в системе", email));
    }
}
