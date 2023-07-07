package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Genres;

import java.util.List;

public interface GenresService {

    public Genres getGenresById(int id);

    public List<Genres> getAll();

}