package ru.yandex.practicum.filmorate.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.LikeException;
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
        log.debug("+ createFilm : {}", film);
        filmValidator.checkFilm(film);
        int id = filmStorage.addFilm(film).getId();
        film.setId(id);
        film.getGenres().sort(Comparator.comparingInt(Genres::getId));
        log.debug("- createFilm : {}", filmStorage.getFilm(film.getId()));
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        log.debug("+ updateFilm : {}", film);
        filmValidator.checkFilm(film);
        Film newFilm = filmStorage.updateFilm(film);
        newFilm.getGenres().sort(Comparator.comparingInt(Genres::getId));
        log.debug("- updateFilm : {}", newFilm);
        return newFilm;
    }

    @Override
    public List<Film> getAllFilms() {
        log.debug("+ getAllFilms");
        var films = filmStorage.getAllFilms();
        log.debug("- getAllFilms : {}", films);
        return films;
    }

    @Override
    public List<Film> getPopularFilms(int count, int genreId, int year) {
        log.debug("- getPopularFilms : count = {}, genreId = {}, year = {}", count, genreId, year);
        var films = filmStorage.getPopularFilms(count, genreId, year);
        log.debug("- getPopularFilms : {}", films);
        return films;
    }

    @Override
    public Film getFilm(int id) {
        log.debug("+ getFilm : id = {}", id);
        Film film = filmStorage.getFilm(id);
        log.debug("- getFilm : {}", film);
        return film;
    }

    @Override
    public Film addLike(int id, int userId) {
        log.debug("+ addLike : id = {}, userId = {}", id, userId);

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
        log.debug("- addLike : {}", updatedFilm);
        return updatedFilm;
    }

    @Override
    public Film deleteLike(int id, int userId) {
        log.debug("+ deleteLike : id = {}, userId = {}", id, userId);
        if (userId <= 0) {
            throw new FilmNotFoundException("Пользователя id = " + userId + " не может быть");
        }
        Film film = filmStorage.getFilm(id);
        filmStorage.removeLike(id, userId);
        filmStorage.updateFilm(film);
        log.debug("- deleteLike: {}", film);
        feedStorage.addToFeedDb(userId, FeedEventType.LIKE, FeedOperation.REMOVE, id);
        return film;
    }

    @Override
    public List<Film> getDirectorFilms(int directorId, String sortBy) {
        log.debug("+ getDirectorFilms : directorId = {} sortBy = {}", directorId, sortBy);
        directorStorage.getDirector(directorId);
        List<Film> films = filmStorage.getDirectorFilms(directorId, sortBy);
        log.debug("- getDirectorFilms : {}", films);
        return films;
    }

    @Override
    public List<Film> getSharedFilms(int userId, int friendId) {
        log.debug("+ getDirectorFilms : userId = {} friendId = {}", userId, friendId);
        if (userId <= 0) {
            throw new FilmNotFoundException("Пользователя id = " + userId + " не может быть");
        }
        if (friendId <= 0) {
            throw new FilmNotFoundException("Пользователя id = " + userId + " не может быть");
        }
        List<Film> films = filmStorage.getSharedFilms(userId, friendId);
        log.debug("- getSharedFilms : {}", films);
        return films;
    }

    @Override
    public void deleteFilm(int id) {
        log.debug("+ deleteFilm : id = {}", id);
        filmStorage.deleteFilm(id);
        log.debug("- deleteFilm : id = {}", id);
    }

    @Override
    public List<Film> searchFilms(String query, List<String> by) {
        log.debug("+ searchFilms : query = " + query + ", by =  " + by);
        List<Film> films = filmStorage.searchFilms(query, by);
        log.debug("- searchFilms : {}", films);
        return films;
    }
}