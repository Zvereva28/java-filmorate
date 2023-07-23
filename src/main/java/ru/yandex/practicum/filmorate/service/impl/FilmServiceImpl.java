package ru.yandex.practicum.filmorate.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genres;
import ru.yandex.practicum.filmorate.model.enums.FeedEventType;
import ru.yandex.practicum.filmorate.model.enums.FeedOperation;
import ru.yandex.practicum.filmorate.service.FilmService;
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

    public FilmServiceImpl(@Qualifier("filmDBStorage") FilmStorage filmStorage, FeedStorage feedStorage, FilmValidator filmValidator) {
        this.filmStorage = filmStorage;
        this.feedStorage = feedStorage;
        this.filmValidator = filmValidator;
    }

    @Override
    public Film addFilm(Film film) {
        log.debug("+ createFilm: {}", film);
        filmValidator.checkFilm(film);
        int id = filmStorage.addFilm(film).getId();
        film.setId(id);
        film.getGenres().sort(Comparator.comparingInt(Genres::getId));
        log.debug("- createFilm: {}", film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        log.debug("+ updateFilm: {}", film);
        filmValidator.checkFilm(film);
        Film oldFilm = filmStorage.updateFilm(film);
        log.debug("- updateFilm: {}", oldFilm);
        return oldFilm;
    }

    @Override
    public List<Film> getAllFilms() {
        var films = filmStorage.getAllFilms();
        log.debug("- allFilms: {}", films);
        return films;
    }

    @Override
    public List<Film> getPopularFilms(int count, int genreId, int year) {
        var films = filmStorage.getPopularFilms(count, genreId, year);
        log.debug("- popularFilms: {}", films);
        return films;
    }

    @Override
    public Film getFilm(int id) {
        Film film = filmStorage.getFilm(id);
        log.debug("- film: {}", film);
        return film;
    }

    @Override
    public Film putLikesFilm(int id, int userId) {
        if (userId <= 0) {
            throw new FilmNotFoundException("Пользователя id = " + userId + " не может быть");
        }
        Film film = filmStorage.getFilm(id);
        filmStorage.addLike(id, userId);
        Film film1 = filmStorage.updateFilm(film);
        log.debug("- putLikesFilm: {}", film1);
        feedStorage.addToFeedDb(userId, FeedEventType.LIKE, FeedOperation.ADD, id);
        return film1;
    }

    @Override
    public Film deleteLikesFilm(int id, int userId) {
        if (userId <= 0) {
            throw new FilmNotFoundException("Пользователя id = " + userId + " не может быть");
        }
        Film film = filmStorage.getFilm(id);
        filmStorage.removeLike(id, userId);
        filmStorage.updateFilm(film);
        log.debug("+ putLikesFilm: {}", film);
        feedStorage.addToFeedDb(userId, FeedEventType.LIKE, FeedOperation.REMOVE, id);
        return film;
    }

    @Override
    public List<Film> getDirectorFilms(int id, String string) {

        return filmStorage.getDirectorFilms(id, string);
    }

    @Override
    public List<Film> getSharedFilms(int userId, int friendId) {
        if (userId <= 0) {
            throw new FilmNotFoundException("Пользователя id = " + userId + " не может быть");
        }
        if (friendId <= 0) {
            throw new FilmNotFoundException("Пользователя id = " + userId + " не может быть");
        }
        return filmStorage.getSharedFilms(userId, friendId);
    }

    @Override
    public void deleteFilm(int id) {
        log.debug("- deleteFilm: {}", id);
        filmStorage.deleteFilm(id);
    }

    @Override
    public List<Film> searchFilms(String query, List<String> by) {
        log.debug("Вы выполнили поиск фильмов по запросу '" + query + "' по полю '" + by + "'");
        return filmStorage.searchFilms(query, by);
    }
}