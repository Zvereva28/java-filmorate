package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.exception.DirectorNotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class DirectorControllerTest {

    private final DirectorStorage directorStorage;

    private final Director director = new Director("Tarantino");

    @Test
    @DisplayName("Получаем всех режиссеров")
    void getAllDirectors() {
        directorStorage.addDirector(director);
        final List<Director> directors = directorStorage.getAllDirectors();
        final int size = directors.size();

        assertNotNull(directors, "Режиссеры не возвращаются.");
        assertEquals(1, size, "Количество режиссеров не совпадает.");
    }

    @Test
    @DisplayName("Получаем режиссера по id")
    void getDirectorById() {
        final Director createdDirector = directorStorage.addDirector(director);
        final int id = createdDirector.getId();

        assertEquals(createdDirector,
                directorStorage.getDirector(id), "Режиссеры не совпадают.");
    }

    @Test
    @DisplayName("Получаем режиссера по не существующему id")
    void getDirectorByNotExistId() {
        final int id = 99;
        final DirectorNotFoundException e = assertThrows(
                DirectorNotFoundException.class,
                () -> directorStorage.getDirector(id)
        );
        assertEquals("Режиссера с id = " + id + " не существует", e.getMessage());
    }

    @Test
    @DisplayName("Создаем режиссера")
    void createDirector() {
        final Director createdDirector = directorStorage.addDirector(director);
        final int id = createdDirector.getId();

        assertEquals(createdDirector,
                directorStorage.getDirector(id), "Режиссеры не совпадают.");
    }

    @Test
    @DisplayName("Обновляем режиссера")
    void updateDirector() {
        directorStorage.addDirector(director).getId();
        final Director upgatedDirector = new Director(1, "Kubrick");

        assertEquals(upgatedDirector,
                directorStorage.updateDirector(upgatedDirector), "Режиссеры разные");
    }

    @Test
    @DisplayName("Удаляем режиссера")
    void deleteDirector() {
        final int id = directorStorage.addDirector(director).getId();
        directorStorage.deleteDirector(director.getId());

        final DirectorNotFoundException e = assertThrows(
                DirectorNotFoundException.class,
                () -> directorStorage.getDirector(id)
        );
        assertEquals("Режиссера с id = " + id + " не существует", e.getMessage());
    }
}
