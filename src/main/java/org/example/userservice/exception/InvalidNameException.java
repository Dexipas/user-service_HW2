package org.example.userservice.exception;

public class InvalidNameException extends ValidationException{
    public InvalidNameException(String name) {
        super(String.format("Некорректное имя \"%s\". Допустимы только буквы. Имя должно начинаться с заглавной буквы",name));
    }
}
