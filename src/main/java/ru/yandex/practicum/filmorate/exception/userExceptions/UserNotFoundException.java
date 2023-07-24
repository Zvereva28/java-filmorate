package ru.yandex.practicum.filmorate.exception.userExceptions;

public class UserNotFoundException extends UserException {

    public UserNotFoundException(String message) {
        super(message);
    }
}