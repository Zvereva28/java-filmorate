package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Genres;

import java.util.List;

public interface GenresStorage {

    public Genres getGenresById(int id);

    public List<Genres> getAll();
}
