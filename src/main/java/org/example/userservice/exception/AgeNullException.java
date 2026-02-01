package org.example.userservice.exception;

public class AgeNullException extends ValidationException{
    public AgeNullException(Integer age) {
        super(String.format("Возраст \"%s\" равен null", age));
    }
}
