package ru.yandex.practicum.filmorate.exception.filmExceptions;

public class FilmNotFoundException extends FilmException {
    public FilmNotFoundException(final String message) {
        super(message);
    }
}