package ru.yandex.practicum.filmorate.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.validators.FilmValidator;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
public class FilmServiceImpl implements FilmService {


    private final FilmStorage filmStorage;
    private FilmValidator filmValidator;

    public FilmServiceImpl( @Qualifier("filmDBStorage")FilmStorage filmStorage, FilmValidator filmValidator) {
        this.filmStorage = filmStorage;
        this.filmValidator = filmValidator;
    }

    @Override
    public Film addFilm(Film film) {
        log.debug("+ createFilm: {}", film);
        filmValidator.checkFilm(film);
        Film newFilm = filmStorage.addFilm(film);
        log.debug("- createFilm: {}", film);
        return newFilm;
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
        var filmsList = filmStorage.getAllFilms();
        log.debug("- allFilms: {}", filmsList);
        return filmsList;
    }

    @Override
    public List<Film> getPopularFilms(int count) {
        var filmsList = filmStorage.getAllFilms();
        Collections.sort(filmsList, new LikesComparator());
        if (count > filmsList.size()) {
            log.debug("- popularFilms: {}", filmsList);
            return filmsList;
        } else {
            List<Film> popularFilms = filmsList.subList(0, count);
            log.debug("- popularFilms: {}", popularFilms);
            return popularFilms;
        }
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
        filmStorage.addLike( id, userId);
        Film film1 = filmStorage.updateFilm(film);
        log.debug("- putLikesFilm: {}", film1);
        return film1;
    }

    @Override
    public Film deleteLikesFilm(int id, int userId) {
        if (userId <= 0) {
            throw new FilmNotFoundException("Пользователя id = " + userId + " не может быть");
        }
        Film film = filmStorage.getFilm(id);
        filmStorage.removeLike( id, userId);
        filmStorage.updateFilm(film);
        log.debug("+ putLikesFilm: {}", film);
        return film;
    }

    class LikesComparator implements Comparator<Film> {
        @Override
        public int compare(Film a, Film b) {
            return Integer.compare( b.getCountLikes(), a.getCountLikes());
        }
    }
}