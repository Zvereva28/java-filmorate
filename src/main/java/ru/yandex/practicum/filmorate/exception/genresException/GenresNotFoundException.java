package ru.yandex.practicum.filmorate.exception.genresException;

import ru.yandex.practicum.filmorate.exception.userExceptions.UserException;

public class GenresNotFoundException extends UserException {

    public GenresNotFoundException(String message) {
        super(message);
    }
}