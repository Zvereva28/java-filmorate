package ru.yandex.practicum.filmorate.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Genres;
import ru.yandex.practicum.filmorate.service.GenresService;
import ru.yandex.practicum.filmorate.storage.GenresStorage;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class GenresServiceImpl implements GenresService {
    private final GenresStorage genresStorage;

    @Override
    public Genres getGenresById(int id) {
        return genresStorage.getGenresById(id);
    }

    @Override
    public List<Genres> getAll() {

        return genresStorage.getAll();
    }
}