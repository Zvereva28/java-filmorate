package ru.yandex.practicum.filmorate.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.filmorate.exception.derectorExceptions.DirectorNotFoundException;
import ru.yandex.practicum.filmorate.exception.filmExceptions.FilmException;
import ru.yandex.practicum.filmorate.exception.filmExceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.genresException.GenresNotFoundException;
import ru.yandex.practicum.filmorate.exception.likeException.LikeException;
import ru.yandex.practicum.filmorate.exception.mpaExceptions.MpaNotFoundException;
import ru.yandex.practicum.filmorate.exception.reviewExceptions.ReviewNotFoundException;
import ru.yandex.practicum.filmorate.exception.userExceptions.UserException;
import ru.yandex.practicum.filmorate.exception.userExceptions.UserNotFoundException;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseBody handleUserException(final UserException e) {
        return new ResponseBody(
                e.getMessage()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseBody handleUserNotFoundException(final UserNotFoundException e) {
        return new ResponseBody(
                e.getMessage()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseBody handleFilmException(final FilmException e) {
        return new ResponseBody(
                e.getMessage()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseBody handleFilmNotFoundException(final FilmNotFoundException e) {
        return new ResponseBody(
                e.getMessage()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseBody handleMpaNotFoundException(final MpaNotFoundException e) {
        return new ResponseBody(
                e.getMessage()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseBody handleMpaNotFoundException(final GenresNotFoundException e) {
        return new ResponseBody(
                e.getMessage()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseBody handleThrowable(final Throwable e) {
        return new ResponseBody(
                "Произошла непредвиденная ошибка."
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseBody reviewNotFoundException(final ReviewNotFoundException e) {
        return new ResponseBody(
                e.getMessage()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseBody directorNotFoundException(final DirectorNotFoundException e) {
        return new ResponseBody(
                e.getMessage()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.OK)
    public ResponseBody likeException(final LikeException e) {
        return new ResponseBody(
                e.getMessage()
        );
    }
}