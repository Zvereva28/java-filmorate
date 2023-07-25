package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;

public interface DirectorService {

    List<Director> getAllDirectors();

    Director addDirector(Director director);

    Director updateDirector(Director director);

    Director getDirectorById(int id);

    void deleteDirector(int id);

}
