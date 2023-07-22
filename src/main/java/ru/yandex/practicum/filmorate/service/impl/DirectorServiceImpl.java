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

    /**
     * Получаем всех режиссеров
     */
    @Override
    public List<Director> getAllDirectors() {
        List<Director> directors = directorStorage.getAllDirectors();
        log.info("Получаем список всех режиссеров");
        return directors;
    }

    /**
     * Создаем режиссера
     */
    @Override
    public Director addDirector(Director director) {
        directorStorage.addDirector(director);
        log.info("Добавлен новый режиссер: id - '{}', name - '{}'", director.getId(), director.getName());
        return director;
    }

    /**
     * Обновляем режиссера
     */
    @Override
    public Director updateDirector(Director director) {
        directorStorage.updateDirector(director);
        log.info("Режиссер - '{}', обновлен ", director);
        return director;
    }

    /**
     * Получаем режиссера по id
     */
    @Override
    public Director getDirector(int id) {
        Director director = directorStorage.getDirector(id);
        log.info("Получаем режиссера с id '{}'", id);
        return director;
    }

    /**
     * Удаляем режиссера
     */
    @Override
    public void deleteDirector(int id) {
        directorStorage.deleteDirector(id);
        log.info("Режиссер c id - '{}', удален ", id);
    }
}
