package ru.yandex.practicum.filmorate.exceptions;

public class UserNotFoundException extends UserException {

    public UserNotFoundException(String message) {
        super(message);
    }
}