package ru.yandex.practicum.filmorate.exception.filmExceptions;

public class FilmException extends RuntimeException {

    public FilmException() {
        super();
    }

    public FilmException(final String message) {
        super(message);
    }

    public FilmException(String message, Throwable cause) {
        super(message, cause);
    }

}