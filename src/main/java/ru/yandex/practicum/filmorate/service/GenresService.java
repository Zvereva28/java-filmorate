package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Genres;

import java.util.List;

public interface GenresService {

    Genres getGenresById(int id);

    List<Genres> getAll();

}