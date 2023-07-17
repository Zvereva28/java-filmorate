package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {

    Film addFilm(Film newFilm);

    Film updateFilm(Film newFilm);

    List<Film> getAllFilms();

    Film getFilm(int id);

    void addLike(Integer filmId, Integer userId);

    void removeLike(Integer filmId, Integer userId);
}