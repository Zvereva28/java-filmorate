package ru.yandex.practicum.filmorate.exception.mpaExceptions;

import ru.yandex.practicum.filmorate.exception.userExceptions.UserException;

public class MpaNotFoundException extends UserException {

    public MpaNotFoundException(String message) {
        super(message);
    }
}