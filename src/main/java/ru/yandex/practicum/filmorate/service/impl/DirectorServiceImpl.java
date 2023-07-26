package ru.yandex.practicum.filmorate.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DirectorServiceImpl implements DirectorService {

    private final DirectorStorage directorStorage;

    @Override
    public List<Director> getAllDirectors() {
        log.info("+ getAllDirectors");
        List<Director> directors = directorStorage.getAllDirectors();
        log.info("- getAllDirectors");
        return directors;
    }

    @Override
    public Director addDirector(Director director) {
        log.info("+ addDirector : {}", director);
        Director newDirector = directorStorage.addDirector(director);
        log.info("- addDirector : {}", newDirector);
        return newDirector;
    }

    @Override
    public Director updateDirector(Director director) {
        log.info("+ updateDirector : {}", director);
        Director newDirector = directorStorage.updateDirector(director);
        log.info("- updateDirector : {}", newDirector);
        return newDirector;
    }

    @Override
    public Director getDirectorById(int id) {
        log.info("+ updateDirector : id = {}", id);
        Director director = directorStorage.getDirector(id);
        log.info("- getDirectorById : {}", director);
        return director;
    }

    @Override
    public void deleteDirector(int id) {
        log.info("+ deleteDirector : id = {}", id);
        directorStorage.deleteDirector(id);
        log.info("- deleteDirector : id = {}", id);
    }
}
