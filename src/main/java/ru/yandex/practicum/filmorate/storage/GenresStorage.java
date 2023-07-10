package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Genres;

import java.util.List;

public interface GenresStorage {

    Genres getGenresById(int id);

    List<Genres> getAll();
}
