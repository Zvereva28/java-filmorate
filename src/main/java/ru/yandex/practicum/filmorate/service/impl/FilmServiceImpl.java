package ru.yandex.practicum.filmorate.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.filmExceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.likeException.LikeException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genres;
import ru.yandex.practicum.filmorate.model.enums.FeedEventType;
import ru.yandex.practicum.filmorate.model.enums.FeedOperation;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.FeedStorage;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.validators.FilmValidator;

import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
public class FilmServiceImpl implements FilmService {
    private final FilmStorage filmStorage;
    private final FeedStorage feedStorage;
    private final FilmValidator filmValidator;
    private final DirectorStorage directorStorage;

    public FilmServiceImpl(FilmStorage filmStorage, FeedStorage feedStorage, FilmValidator filmValidator,
                           DirectorStorage directorStorage) {
        this.filmStorage = filmStorage;
        this.feedStorage = feedStorage;
        this.filmValidator = filmValidator;
        this.directorStorage = directorStorage;
    }

    @Override
    public Film addFilm(Film film) {
        log.info("+ createFilm : {}", film);
        filmValidator.checkFilm(film);
        int id = filmStorage.addFilm(film).getId();
        film.setId(id);
        film.getGenres().sort(Comparator.comparingInt(Genres::getId));
        log.info("- createFilm : {}", filmStorage.getFilm(film.getId()));
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        log.info("+ updateFilm : {}", film);
        filmValidator.checkFilm(film);
        Film oldFilm = filmStorage.updateFilm(film);
        log.info("- updateFilm : {}", oldFilm);
        return oldFilm;
    }

    @Override
    public List<Film> getAllFilms() {
        log.info("+ getAllFilms");
        var films = filmStorage.getAllFilms();
        log.info("- getAllFilms : {}", films);
        return films;
    }

    @Override
    public List<Film> getPopularFilms(int count, int genreId, int year) {
        log.info("- getPopularFilms : count = {}, genreId = {}, year = {}", count, genreId, year);
        var films = filmStorage.getPopularFilms(count, genreId, year);
        log.info("- getPopularFilms : {}", films);
        return films;
    }

    @Override
    public Film getFilm(int id) {
        log.info("+ getFilm : id = {}", id);
        Film film = filmStorage.getFilm(id);
        log.info("- getFilm : {}", film);
        return film;
    }

    @Override
    public Film addLike(int id, int userId) {
        log.info("+ addLike : id = {}, userId = {}", id, userId);

        if (userId <= 0) {
            throw new FilmNotFoundException("Пользователя id = " + userId + " не может быть");
        }

        feedStorage.addToFeedDb(userId, FeedEventType.LIKE, FeedOperation.ADD, id);
        if (!filmValidator.isNoLike(userId, id)) {
            throw new LikeException("Пользователь с id = " + userId + " уже поставил лайк фильму id = " + id);
        }

        Film film = filmStorage.getFilm(id);
        filmStorage.addLike(id, userId);
        Film updatedFilm = filmStorage.updateFilm(film);
        log.info("- addLike : {}", updatedFilm);
        return updatedFilm;
    }

    @Override
    public Film deleteLike(int id, int userId) {
        log.info("+ deleteLike : id = {}, userId = {}", id, userId);
        if (userId <= 0) {
            throw new FilmNotFoundException("Пользователя id = " + userId + " не может быть");
        }
        Film film = filmStorage.getFilm(id);
        filmStorage.removeLike(id, userId);
        filmStorage.updateFilm(film);
        log.info("- deleteLike: {}", film);
        feedStorage.addToFeedDb(userId, FeedEventType.LIKE, FeedOperation.REMOVE, id);
        return film;
    }

    @Override
    public List<Film> getDirectorFilms(int directorId, String sortBy) {
        log.info("+ getDirectorFilms : directorId = {} sortBy = {}", directorId, sortBy);
        directorStorage.getDirector(directorId);
        List<Film> answer = filmStorage.getDirectorFilms(directorId, sortBy);
        log.info("- getDirectorFilms : {}", answer);
        return answer;
    }

    @Override
    public List<Film> getSharedFilms(int userId, int friendId) {
        log.info("+ getDirectorFilms : userId = {} friendId = {}", userId, friendId);
        if (userId <= 0) {
            throw new FilmNotFoundException("Пользователя id = " + userId + " не может быть");
        }
        if (friendId <= 0) {
            throw new FilmNotFoundException("Пользователя id = " + userId + " не может быть");
        }
        List<Film> answer = filmStorage.getSharedFilms(userId, friendId);
        log.info("- getSharedFilms : {}", answer);
        return answer;
    }

    @Override
    public void deleteFilm(int id) {
        log.info("+ deleteFilm : id = {}", id);
        filmStorage.deleteFilm(id);
        log.info("- deleteFilm : id = {}", id);
    }

    @Override
    public List<Film> searchFilms(String query, List<String> by) {
        log.info("+ searchFilms : query = " + query + ", by =  " + by);
        List<Film> answer = filmStorage.searchFilms(query, by);
        log.info("- searchFilms : {}", answer);
        return answer;
    }
}