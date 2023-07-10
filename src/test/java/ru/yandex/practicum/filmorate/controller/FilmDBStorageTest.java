package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.exception.FilmException;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.impl.dao.FilmDBStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class FilmDBStorageTest {

    private final FilmDBStorage filmDBStorage;

    @Test
    @DisplayName("Список фильмов, когда он пуст")
    void findAllNullArray() {
        assertEquals(0, filmDBStorage.getAllFilms().size());
    }

    @Test
    @DisplayName("Список фильмов")
    void findAllStandard() {
        filmDBStorage.addFilm(new Film(0, "dolore", "description description", LocalDate.of(1995, 11, 28), 50, new Mpa(1), 0));
        filmDBStorage.addFilm(new Film(0, "dolore2", "description description", LocalDate.of(1985, 12, 11), 66, new Mpa(1), 0));
        assertEquals(2, filmDBStorage.getAllFilms().size());
    }

    @Test
    @DisplayName("Создание фильмов")
    void createStandard() {
        assertEquals(1, filmDBStorage.addFilm(new Film(0, "dolore", "description description", LocalDate.of(1995, 11, 28), 50, new Mpa(1), 0)).getId());
    }

    @Test
    @DisplayName("Обновление фильмов")
    void updateStandard() {
        filmDBStorage.addFilm(new Film(0, "dolore", "description description", LocalDate.of(1995, 11, 28), 50, new Mpa(1), 0));
        assertEquals("newName", filmDBStorage.updateFilm(new Film(1, "newName", "description description", LocalDate.of(1995, 11, 28), 50, new Mpa(1), 0)).getName());
    }

    @Test
    @DisplayName("Обновление фильмов, id не верный")
    void updateExceptionIDError() {
        FilmException exception = assertThrows(
                FilmNotFoundException.class,
                generateExecutableIDError()
        );
        assertEquals("Фильм с id = 99 не существует", exception.getMessage());
    }

    private Executable generateExecutableIDError() {
        return () -> filmDBStorage.updateFilm(new Film(99, "newName", "description description", LocalDate.of(1995, 11, 28), 50, new Mpa(1), 0));
    }

}