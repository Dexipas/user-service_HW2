package org.example.userservice.exception;

public class NameEmptyOrNullException extends ValidationException{
    public NameEmptyOrNullException(String name) {
        super(String.format("Имя \"%s\" пустое или равно null", name));
    }
}
