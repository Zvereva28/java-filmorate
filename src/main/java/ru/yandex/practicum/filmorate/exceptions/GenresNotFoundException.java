package ru.yandex.practicum.filmorate.exceptions;

public class GenresNotFoundException extends UserException {

    public GenresNotFoundException(String message) {
        super(message);
    }
}