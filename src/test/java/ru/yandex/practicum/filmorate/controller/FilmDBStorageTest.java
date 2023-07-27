package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.exception.filmExceptions.FilmException;
import ru.yandex.practicum.filmorate.exception.filmExceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.impl.dao.DirectorDBStorage;
import ru.yandex.practicum.filmorate.storage.impl.dao.FilmDBStorage;
import ru.yandex.practicum.filmorate.storage.impl.dao.UserDBStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class FilmDBStorageTest {

    private final FilmDBStorage filmDBStorage;

    private final DirectorDBStorage directorDBStorage;

    private final UserDBStorage userDBStorage;

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

    @Test
    @DisplayName("Обновление фильма, добавление режиссера")
    void updateFilmAddDirector() {
        final Film film = filmDBStorage.addFilm(new Film(0, "dolore", "description description", LocalDate.of(1995, 11, 28), 50, new Mpa(1), 0));
        final Director director = directorDBStorage.addDirector(new Director("Tarantino"));
        final List<Director> directors = new ArrayList<>();
        directors.add(director);
        film.addDirector(director);
        final Film updatedFilm = filmDBStorage.updateFilm(film);

        assertEquals(directors, updatedFilm.getDirectors());
    }

    @Test
    @DisplayName("Получение списка фильмов режиссера, отсортированных по году")
    void getDirectorFilmsSortedByYear() {
        final Director director = directorDBStorage.addDirector(new Director("Tarantino"));
        final int directorId = director.getId();
        final Film film1 = new Film(1, "Interstellar", "Description", LocalDate.of(2014, 9, 21), 169, new Mpa(1), 0);
        final Film film2 = new Film(2, "Alien Covenant", "Description", LocalDate.of(2017, 9, 21), 110, new Mpa(4), 0);
        final Film film3 = new Film(3, "Dune", "Description", LocalDate.of(2021, 9, 21), 145, new Mpa(3), 0);
        film1.addDirector(director);
        film2.addDirector(director);
        film3.addDirector(director);
        filmDBStorage.addFilm(film1);
        filmDBStorage.addFilm(film2);
        filmDBStorage.addFilm(film3);
        final List<Film> filmsByYear = new ArrayList<>();
        filmsByYear.add(film1);
        filmsByYear.add(film2);
        filmsByYear.add(film3);

        assertEquals(filmsByYear, filmDBStorage.getDirectorFilms(directorId, "year"));
    }

    @Test
    @DisplayName("Получение списка фильмов режиссера, отсортированных по году")
    void getDirectorFilmsSortedByLikes() {
        final Director director = directorDBStorage.addDirector(new Director("Tarantino"));
        final int directorId = director.getId();
        final Film film1 = filmDBStorage.addFilm(new Film(1, "Interstellar", "Description", LocalDate.of(2014, 9, 21), 169, new Mpa(1), 1));
        final Film film2 = filmDBStorage.addFilm(new Film(2, "Alien Covenant", "Description", LocalDate.of(2017, 9, 21), 110, new Mpa(4), 0));
        final Film film3 = filmDBStorage.addFilm(new Film(3, "Dune", "Description", LocalDate.of(2021, 9, 21), 145, new Mpa(3), 2));
        film1.addDirector(director);
        film2.addDirector(director);
        film3.addDirector(director);
        userDBStorage.createUser(new User(1, "dolore", "NickName", "Nick Name", LocalDate.of(1995, 11, 28)));
        userDBStorage.createUser(new User(2, "dolore2", "LogName", "NickName", LocalDate.of(1985, 11, 28)));
        filmDBStorage.addLike(1, 1);
        filmDBStorage.addLike(3, 1);
        filmDBStorage.addLike(3, 2);
        filmDBStorage.updateFilm(film1);
        filmDBStorage.updateFilm(film2);
        filmDBStorage.updateFilm(film3);
        final List<Film> filmsByLikes = new ArrayList<>();
        filmsByLikes.add(film3);
        filmsByLikes.add(film1);
        filmsByLikes.add(film2);

        assertEquals(filmsByLikes, filmDBStorage.getDirectorFilms(directorId, "likes"));
    }

    private Executable generateExecutableIDError() {
        return () -> filmDBStorage.updateFilm(new Film(99, "newName", "description description", LocalDate.of(1995, 11, 28), 50, new Mpa(1), 0));
    }

}