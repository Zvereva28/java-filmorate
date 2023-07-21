package ru.yandex.practicum.filmorate.exception;

public class DirectorException extends RuntimeException {

    public DirectorException() {
        super();
    }

    public DirectorException(final String message) {
        super(message);
    }
}
