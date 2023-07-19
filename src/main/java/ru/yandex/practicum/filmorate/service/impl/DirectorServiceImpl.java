package ru.yandex.practicum.filmorate.service.impl;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.DirectorService;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;

import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
public class DirectorServiceImpl implements DirectorService {

    private final DirectorStorage directorStorage;

    /** Получаем всех режиссеров */
    @Override
    public List<Director> getAllDirectors() {
        List<Director> directors = directorStorage.getAllDirectors();
        return directors;
    }

    /** Создаем режиссера */
    @Override
    public Director addDirector(Director director) {
        return directorStorage.addDirector(director);
    }

    /** Обновляем режиссера */
    @Override
    public Director updateDirector(Director director) {
        return directorStorage.updateDirector(director);
    }

    /** Получаем режиссера по id */
    @Override
    public Director getDirector(int id) {
        return directorStorage.getDirector(id);
    }

    /** Удаляем режиссера */
    @Override
    public void deleteDirector(int id) {
        directorStorage.deleteDirector(id);
    }
}
