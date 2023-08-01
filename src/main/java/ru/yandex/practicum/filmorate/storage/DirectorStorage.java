package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;

public interface DirectorStorage {

    Director addDirector(Director director);

    Director getDirector(int id);

    List<Director> getAllDirectors();

    Director updateDirector(Director director);

    void deleteDirector(int id);
}
