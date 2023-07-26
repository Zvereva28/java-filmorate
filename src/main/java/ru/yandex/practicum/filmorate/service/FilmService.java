package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmService {

    Film addFilm(Film newFilm);

    Film updateFilm(Film newFilm);

    List<Film> getAllFilms();

    List<Film> getPopularFilms(int count, int genreId, int year);

    Film getFilm(int id);

    Film addLike(int id, int userId);

    Film deleteLike(int id, int userId);

    List<Film> getDirectorFilms(int directorId, String sortBy);

    List<Film> getSharedFilms(int userId, int friendId);

    List<Film> searchFilms(String query, List<String> by);

    void deleteFilm(int id);
}