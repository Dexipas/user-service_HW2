package org.example.userservice.exception;


public class InvalidIdException extends ValidationException{
    public InvalidIdException(String id) {
        super(String.format("Некорректный id \"%s\". Формат xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx, где x - a-z и 0-9", id));
    }
}
