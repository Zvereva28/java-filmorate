package ru.yandex.practicum.filmorate.exceptions;

public class ReviewException extends RuntimeException {
    public ReviewException() {
    }

    public ReviewException(String message) {
        super(message);
    }

    public ReviewException(String message, Throwable cause) {
        super(message, cause);
    }
}
