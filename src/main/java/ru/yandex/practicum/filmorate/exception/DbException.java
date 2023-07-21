package ru.yandex.practicum.filmorate.exception;

public class DbException extends RuntimeException {

    public DbException() {
        super();
    }

    public DbException(final String message) {
        super(message);
    }

    public DbException(String message, Throwable cause) {
        super(message, cause);
    }
}
