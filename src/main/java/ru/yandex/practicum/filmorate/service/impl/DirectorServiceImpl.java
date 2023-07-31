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
        log.debug("+ getAllDirectors");
        List<Director> directors = directorStorage.getAllDirectors();
        log.debug("- getAllDirectors");
        return directors;
    }

    @Override
    public Director addDirector(Director director) {
        log.debug("+ addDirector : {}", director);
        Director newDirector = directorStorage.addDirector(director);
        log.debug("- addDirector : {}", newDirector);
        return newDirector;
    }

    @Override
    public Director updateDirector(Director director) {
        log.debug("+ updateDirector : {}", director);
        Director newDirector = directorStorage.updateDirector(director);
        log.debug("- updateDirector : {}", newDirector);
        return newDirector;
    }

    @Override
    public Director getDirectorById(int id) {
        log.debug("+ updateDirector : id = {}", id);
        Director director = directorStorage.getDirector(id);
        log.debug("- getDirectorById : {}", director);
        return director;
    }

    @Override
    public void deleteDirector(int id) {
        log.debug("+ deleteDirector : id = {}", id);
        directorStorage.deleteDirector(id);
        log.debug("- deleteDirector : id = {}", id);
    }
}
