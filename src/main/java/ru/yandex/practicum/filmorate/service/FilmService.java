package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmService {

    public Film addFilm(Film newFilm);

    public Film updateFilm(Film newFilm);

    public List<Film> getAllFilms();

    public List<Film> getPopularFilms(int count);

    public Film getFilm(int id);

    public Film putLikesFilm(int id, int userId);

    public Film deleteLikesFilm(int id, int userId);
}
