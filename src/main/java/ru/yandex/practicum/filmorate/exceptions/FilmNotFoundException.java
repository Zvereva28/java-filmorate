package ru.yandex.practicum.filmorate.exceptions;

public class FilmNotFoundException extends FilmException {
    public FilmNotFoundException(final String message) {
        super(message);
    }
}