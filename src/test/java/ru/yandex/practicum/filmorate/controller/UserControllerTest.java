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
import ru.yandex.practicum.filmorate.exceptions.UserException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.enums.FeedEventType;
import ru.yandex.practicum.filmorate.model.enums.FeedOperation;
import ru.yandex.practicum.filmorate.service.impl.UserServiceImpl;
import ru.yandex.practicum.filmorate.storage.impl.dao.FeedDbStorage;
import ru.yandex.practicum.filmorate.storage.impl.dao.FilmDBStorage;
import ru.yandex.practicum.filmorate.storage.impl.dao.UserDBStorage;
import ru.yandex.practicum.filmorate.validators.UserValidator;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class UserControllerTest {

    @Autowired
    private final JdbcTemplate jdbcTemplate;
    @Autowired
    private UserController userController;
    @Autowired
    private FeedDbStorage feedDbStorage;

    UserControllerTest(@Autowired JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @BeforeEach
    public void setUp() {
        userController = new UserController(new UserServiceImpl(new UserDBStorage(jdbcTemplate), new FilmDBStorage(jdbcTemplate), new FeedDbStorage(jdbcTemplate), new UserValidator()));
    }

    @Test
    @DisplayName("Список пользователей, когда он пуст")
    void findAllNullArray() {
        assertEquals(0, userController.getAllUsers().size());
    }

    @Test
    @DisplayName("Список пользователей")
    void findAllStandard() {
        userController.addUser(new User(0, "dolore", "NickName", "Nick Name", LocalDate.of(1995, 11, 28)));
        userController.addUser(new User(0, "dolore2", "LogName", "NickName", LocalDate.of(1985, 11, 28)));
        assertEquals(2, userController.getAllUsers().size());
    }

    @Test
    @DisplayName("Создание пользователя")
    void createStandard() {
        assertEquals(1, userController.addUser(new User(0, "dolore", "NickName", "Nick Name", LocalDate.now())).getId());
    }

    @Test
    @DisplayName("Создание пользователя, Login содержит пробел")
    void createExceptionUserName() {
        UserException exception = assertThrows(
                UserException.class,
                generateExecutableToUserName()
        );
        assertEquals("Login не должен содержать пробелы", exception.getMessage());
    }

    private Executable generateExecutableToUserName() {
        return () -> userController.addUser(new User(0, "fdhfgj", "Status Task.NEW", "635", LocalDate.of(2018, 5, 11)));
    }

    @Test
    @DisplayName("Создание пользователя, Не верная дата рождения")
    void createExceptionBirthday() {
        UserException exception = assertThrows(
                UserException.class,
                generateExecutableToBirthday()
        );
        assertEquals("Не верная дата рождения", exception.getMessage());
    }

    private Executable generateExecutableToBirthday() {
        return () -> userController.addUser(new User(0, "fdhfgj", "StatusTask.NEW", "635", LocalDate.now().plusDays(1)));
    }

    @Test
    @DisplayName("Обновление пользователя")
    void updateStandard() {
        userController.addUser(new User(0, "dolore", "NickName", "Nick Name", LocalDate.of(1995, 11, 28)));
        assertEquals("newEmail", userController.updateUser(new User(1, "newEmail", "NickName", "Nick Name", LocalDate.of(1995, 11, 28))).getEmail());
    }

    @Test
    @DisplayName("Обновление пользователя, Login содержит пробел")
    void updateExceptionUserName() {
        userController.addUser(new User(0, "dolore", "NickName", "Nick Name", LocalDate.of(1995, 11, 28)));
        UserException exception = assertThrows(
                UserException.class,
                generateUpdateExecutableToUserLogin()
        );
        assertEquals("Login не должен содержать пробелы", exception.getMessage());
    }

    private Executable generateUpdateExecutableToUserLogin() {
        return () -> userController.updateUser(new User(1, "fdhfgj", "Status Task.NEW", "635", LocalDate.of(2018, 5, 11)));
    }

    @Test
    @DisplayName("Обновление пользователя, Не верная дата рождения")
    void updateExceptionBirthday() {
        userController.addUser(new User(0, "dolore", "NickName", "Nick Name", LocalDate.of(1995, 11, 28)));
        UserException exception = assertThrows(
                UserException.class,
                generateUpdateExecutableToBirthday()
        );
        assertEquals("Не верная дата рождения", exception.getMessage());
    }

    private Executable generateUpdateExecutableToBirthday() {
        return () -> userController.updateUser(new User(1, "fdhfgj", "StatusTask.NEW", "635", LocalDate.now().plusDays(1)));
    }

    @Test
    @DisplayName("Обновление пользователя, id не верный")
    void updateExceptionIDError() {
        UserException exception = assertThrows(
                UserException.class,
                generateUpdateExecutableIDError()
        );
        assertEquals("Пользователя с id = 99 не существует", exception.getMessage());
    }

    @Test
    void putFriendsTestFeed() {
        userController.addUser(new User(1, "dolore", "NickName", "Nick Name", LocalDate.of(1995, 11, 28)));
        userController.addUser(new User(2, "dolore2", "NickName2", "Nick Name2", LocalDate.of(1995, 11, 28)));
        userController.addFriend(1, 2);
        assertEquals(FeedEventType.FRIEND, feedDbStorage.getFeedByUserId(1).get(0).getEventType());
    }

    @Test
    void deleteFriendsTestFeed() {
        userController.addUser(new User(1, "dolore", "NickName", "Nick Name", LocalDate.of(1995, 11, 28)));
        userController.addUser(new User(2, "dolore2", "NickName2", "Nick Name2", LocalDate.of(1995, 11, 28)));
        userController.addFriend(1, 2);
        userController.deleteFriend(1, 2);
        assertEquals(FeedOperation.REMOVE, feedDbStorage.getFeedByUserId(1).get(1).getOperation());
    }

    @Test
    void getFeedByUserId() {
        userController.addUser(new User(1, "dolore", "NickName", "Nick Name", LocalDate.of(1995, 11, 28)));
        userController.addUser(new User(2, "dolore2", "NickName2", "Nick Name2", LocalDate.of(1995, 11, 28)));
        userController.addFriend(1, 2);
        assertEquals(1, feedDbStorage.getFeedByUserId(1).size());
    }

    private Executable generateUpdateExecutableIDError() {
        return () -> userController.updateUser(new User(99, "fdhfgj", "StatusTask.NEW", "635", LocalDate.of(1995, 11, 28)));
    }


}