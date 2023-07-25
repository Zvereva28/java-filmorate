package ru.yandex.practicum.filmorate.exception.likeException;

public class LikeException extends RuntimeException {
    public LikeException() {
    }

    public LikeException(String message) {
        super(message);
    }

    public LikeException(String message, Throwable cause) {
        super(message, cause);
    }
}
