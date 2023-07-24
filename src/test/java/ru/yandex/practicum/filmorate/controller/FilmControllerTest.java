package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.exception.FilmException;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.enums.FeedEventType;
import ru.yandex.practicum.filmorate.model.enums.FeedOperation;
import ru.yandex.practicum.filmorate.service.impl.FilmServiceImpl;
import ru.yandex.practicum.filmorate.service.impl.UserServiceImpl;
import ru.yandex.practicum.filmorate.storage.impl.dao.FeedDbStorage;
import ru.yandex.practicum.filmorate.storage.impl.dao.FilmDBStorage;
import ru.yandex.practicum.filmorate.storage.impl.dao.LikesDBStorage;
import ru.yandex.practicum.filmorate.storage.impl.dao.UserDBStorage;
import ru.yandex.practicum.filmorate.validators.FeedValidator;
import ru.yandex.practicum.filmorate.validators.FilmValidator;
import ru.yandex.practicum.filmorate.validators.UserValidator;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class FilmControllerTest {
    @Autowired
    private final JdbcTemplate jdbcTemplate;
    @Autowired
    private FilmController filmController;
    @Autowired
    private UserController userController;
    @Autowired
    private FeedDbStorage feedDbStorage;

    FilmControllerTest(@Autowired JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @BeforeEach
    public void setUp() {
        filmController = new FilmController(new FilmServiceImpl(new FilmDBStorage(jdbcTemplate), new FeedDbStorage(jdbcTemplate, new FeedValidator(jdbcTemplate)), new FilmValidator(jdbcTemplate)));
    }

    @Test
    @DisplayName("Список фильмов, когда он пуст")
    void findAllNullArray() {
        assertEquals(0, filmController.getFilms().size());
    }

    @Test
    @DisplayName("Список фильмов")
    void findAllStandard() {
        filmController.postFilm(new Film(0, "dolore", "description description", LocalDate.of(1995, 11, 28), 50, new Mpa(1), 0));
        filmController.postFilm(new Film(0, "dolore2", "description description", LocalDate.of(1985, 12, 11), 66, new Mpa(1), 0));
        assertEquals(2, filmController.getFilms().size());
    }

    @Test
    @DisplayName("Создание фильмов")
    void createStandard() {
        assertEquals(1, filmController.postFilm(new Film(0, "dolore", "description description", LocalDate.of(1995, 11, 28), 50, new Mpa(1), 0)).getId());
    }


    @Test
    @DisplayName("Создание фильма, Название фильма пустое")
    void createExceptionToName() {
        FilmException exception = assertThrows(
                FilmException.class,
                generateExecutableToName()
        );
        assertEquals("Название фильма не должно быть пустым", exception.getMessage());
    }

    private Executable generateExecutableToName() {
        return () -> filmController.postFilm(new Film(0, " ", "description description", LocalDate.of(1985, 12, 11), 66, new Mpa(1), 0));
    }


    @Test
    @DisplayName("Создание фильма, Описание фильма больше 200 символов")
    void createExceptionDescription() {
        FilmException exception = assertThrows(
                FilmException.class,
                generateExecutableToDescription()
        );
        assertEquals("Описание фильма должно быть не больше 200 символов", exception.getMessage());
    }

    private Executable generateExecutableToDescription() {
        return () -> filmController.postFilm(new Film(0, "filmName", "description description description description description description description description description description description description description descriptionescription description description description description description description description", LocalDate.of(1985, 12, 11), 66, new Mpa(1), 0));
    }

    @Test
    @DisplayName("Создание фильма, Продолжительность меньше 0")
    void createExceptionDuration() {
        FilmException exception = assertThrows(
                FilmException.class,
                generateExecutableToDuration()
        );
        assertEquals("Продолжительность фильма должна быть больше 0", exception.getMessage());
    }

    private Executable generateExecutableToDuration() {
        return () -> filmController.postFilm(new Film(0, "filmName", "description description", LocalDate.of(1985, 12, 11), -66, new Mpa(1), 0));
    }

    @Test
    @DisplayName("Создание фильма, Дата релиза не корректна")
    void createExceptionReleaseDate() {
        FilmException exception = assertThrows(
                FilmException.class,
                generateExecutableToReleaseDate()
        );
        assertEquals("Не верная дата релиза", exception.getMessage());
    }

    private Executable generateExecutableToReleaseDate() {
        return () -> filmController.postFilm(new Film(0, "filmName", "description description", LocalDate.of(1885, 12, 11), 66, new Mpa(1), 0));
    }

    @Test
    @DisplayName("Обновление фильмов")
    void updateStandard() {
        filmController.postFilm(new Film(0, "dolore", "description description", LocalDate.of(1995, 11, 28), 50, new Mpa(1), 0));
        assertEquals("newName", filmController.putFilm(new Film(1, "newName", "description description", LocalDate.of(1995, 11, 28), 50, new Mpa(1), 0)).getName());
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
        return () -> filmController.putFilm(new Film(99, "newName", "description description", LocalDate.of(1995, 11, 28), 50, new Mpa(1), 0));
    }

    @Test
    @DisplayName("Обновление фильмов, Описание фильма больше 200 символов")
    void createUpdateExceptionDescription() {
        filmController.postFilm(new Film(0, "dolore", "description description", LocalDate.of(1995, 11, 28), 50, new Mpa(1), 0));

        FilmException exception = assertThrows(
                FilmException.class,
                generateUpdateExecutableToDescription()
        );
        assertEquals("Описание фильма должно быть не больше 200 символов", exception.getMessage());
    }

    private Executable generateUpdateExecutableToDescription() {
        return () -> filmController.putFilm(new Film(1, "filmName", "description description description description description description description description description description description description description descriptionescription description description description description description description description", LocalDate.of(1985, 12, 11), 66, new Mpa(1), 0));
    }

    @Test
    @DisplayName("Обновление фильмов, Продолжительность меньше 0")
    void updateExceptionDuration() {
        filmController.postFilm(new Film(0, "dolore", "description description", LocalDate.of(1995, 11, 28), 50, new Mpa(1), 0));

        FilmException exception = assertThrows(
                FilmException.class,
                generateUpdateExecutableToDuration()
        );
        assertEquals("Продолжительность фильма должна быть больше 0", exception.getMessage());
    }

    private Executable generateUpdateExecutableToDuration() {
        return () -> filmController.putFilm(new Film(1, "filmName", "description description", LocalDate.of(1985, 12, 11), -66, new Mpa(1), 0));
    }

    @Test
    @DisplayName("Обновление фильмов, Дата релиза не корректна")
    void updateExceptionReleaseDate() {
        filmController.postFilm(new Film(0, "dolore", "description description", LocalDate.of(1995, 11, 28), 50, new Mpa(1), 0));

        FilmException exception = assertThrows(
                FilmException.class,
                generateUpdateExecutableToReleaseDate()
        );
        assertEquals("Не верная дата релиза", exception.getMessage());
    }

    @Test
    void putLikesFilmByIdFeed() {
        userController = new UserController(new UserServiceImpl(new UserDBStorage(jdbcTemplate), new LikesDBStorage(jdbcTemplate, new FilmDBStorage(jdbcTemplate)), new FeedDbStorage(jdbcTemplate, new FeedValidator(jdbcTemplate)), new UserValidator()));
        userController.postUser(new User(1, "dolore", "NickName", "Nick Name", LocalDate.of(1995, 11, 28)));
        filmController.postFilm(new Film(1, "dolore", "description description", LocalDate.of(1995, 11, 28), 50, new Mpa(1), 0));
        filmController.putLikesFilmById(1, 1);
        assertEquals(FeedEventType.LIKE, feedDbStorage.getFeedByUserId(1).get(0).getEventType());
    }

    @Test
    void deleteLikesFilmByIdFeed() {
        userController = new UserController(new UserServiceImpl(new UserDBStorage(jdbcTemplate), new LikesDBStorage(jdbcTemplate, new FilmDBStorage(jdbcTemplate)), new FeedDbStorage(jdbcTemplate, new FeedValidator(jdbcTemplate)), new UserValidator()));
        userController.postUser(new User(1, "dolore", "NickName", "Nick Name", LocalDate.of(1995, 11, 28)));
        filmController.postFilm(new Film(1, "dolore", "description description", LocalDate.of(1995, 11, 28), 50, new Mpa(1), 0));
        filmController.putLikesFilmById(1, 1);
        filmController.deleteLikesFilmById(1, 1);
        feedDbStorage.getFeedByUserId(1);
        assertEquals(FeedOperation.REMOVE, feedDbStorage.getFeedByUserId(1).get(1).getOperation());
    }

    private Executable generateUpdateExecutableToReleaseDate() {
        return () -> filmController.putFilm(new Film(1, "filmName", "description description", LocalDate.of(1885, 12, 11), 66, new Mpa(1), 0));
    }


}