package ru.yandex.practicum.filmorate.service.film;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.validators.FilmValidator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class FilmService implements FilmServiceInterface {
    private final FilmStorage filmStorage;
    private FilmValidator filmValidator;

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
        log.debug("+ allFilms: {}", filmsList);
        return filmsList;
    }

    @Override
    public List<Film> getPopularFilms(int count) {
        var filmsList = filmStorage.getAllFilms();
        List<Film> popularFilms = new ArrayList<>();
        Collections.sort(filmsList, new LikesComparator());
        for (int i = 0; i < filmsList.size(); i++) {
            if (i >= count) {
                break;
            } else popularFilms.add(filmsList.get(i));
        }
        log.debug("+ popularFilms: {}", popularFilms);
        return popularFilms;
    }

    @Override
    public Film getFilm(int id) {
        Film film = filmStorage.getFilm(id);
        log.debug("+ films: {}", film);
        return film;
    }

    @Override
    public Film putLikesFilm(int id, int userId) {
        if (userId <= 0) {
            throw new FilmNotFoundException("Пользователя id = " + userId + " не может быть");
        }
        Film film = filmStorage.getFilm(id);
        film.getLikes().add(userId);
        filmStorage.updateFilm(film);
        log.debug("+ putLikesFilm: {}", film);
        return film;
    }

    @Override
    public Film deleteLikesFilm(int id, int userId) {
        if (userId <= 0) {
            throw new FilmNotFoundException("Пользователя id = " + userId + " не может быть");
        }
        Film film = filmStorage.getFilm(id);
        film.getLikes().remove(userId);
        filmStorage.updateFilm(film);
        log.debug("+ putLikesFilm: {}", film);
        return film;
    }

    class LikesComparator implements Comparator<Film> {
        @Override
        public int compare(Film a, Film b) {
            return Integer.compare(b.getLikes().size(), a.getLikes().size());
        }
    }
}
