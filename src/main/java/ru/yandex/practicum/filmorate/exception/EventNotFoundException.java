package ru.yandex.practicum.filmorate.exception;

public class EventNotFoundException extends RuntimeException {
    public EventNotFoundException() {
        super();
    }

    public EventNotFoundException(String message) {
        super(message);
    }

    public EventNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
