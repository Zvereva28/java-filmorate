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
    public Genres getGenre(int id) {
        log.info("+ getGenre : id = {}", id);
        Genres answer = genresStorage.getGenresById(id);
        log.info("- getGenre : {}", answer);
        return answer;
    }

    @Override
    public List<Genres> getAllGenres() {
        log.info("+ getAllGenres");
        List<Genres> answer = genresStorage.getAll();
        log.info("- getAllGenres : {}", answer);
        return answer;
    }
}