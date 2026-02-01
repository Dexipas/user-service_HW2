package org.example.userservice.exception;

public class InvalidAgeException extends ValidationException {
    public InvalidAgeException(Integer age) {
        super(String.format("Некорректный возраст \"%s\". Допустимый диапазон 1 до 100", age));
    }
}
