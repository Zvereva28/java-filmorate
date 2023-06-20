package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {

    public Film addFilm(Film newFilm);

    public Film updateFilm(Film newFilm);

    public List<Film> getAllFilms();

    public Film getFilm(int id);

}