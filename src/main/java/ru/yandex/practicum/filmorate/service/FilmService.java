package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmService {

    Film addFilm(Film newFilm);

    Film updateFilm(Film newFilm);

    List<Film> getAllFilms();

    List<Film> getPopularFilms(int count);

    Film getFilm(int id);

    Film putLikesFilm(int id, int userId);

    Film deleteLikesFilm(int id, int userId);
}