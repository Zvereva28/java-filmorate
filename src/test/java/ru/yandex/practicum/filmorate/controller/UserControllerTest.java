package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import ru.yandex.practicum.filmorate.exception.UserException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UserControllerTest {

    private UserController userController;

    @BeforeEach
    public void setUp() {
        userController = new UserController();
    }

    @Test
    @DisplayName("Список пользователей, когда он пуст")
    void findAllNullArray() {
        assertEquals(0, userController.findAllUser().size());
    }

    @Test
    @DisplayName("Список пользователей")
    void findAllStandard() {
        userController.createUser(new User(0, "dolore", "NickName", "Nick Name", LocalDate.of(1995, 11, 28)));
        userController.createUser(new User(0, "dolore2", "LogName", "NickName", LocalDate.of(1985, 11, 28)));
        assertEquals(2, userController.findAllUser().size());
    }

    @Test
    @DisplayName("Создание пользователя")
    void createStandard() {
        assertEquals(1, userController.createUser(new User(0, "dolore", "NickName", "Nick Name", LocalDate.now())).getId());
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
        return () -> userController.createUser(new User(0, "fdhfgj", "Status Task.NEW", "635", LocalDate.of(2018, 5, 11)));
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
        return () -> userController.createUser(new User(0, "fdhfgj", "StatusTask.NEW", "635", LocalDate.now().plusDays(1)));
    }

    @Test
    @DisplayName("Обновление пользователя")
    void updateStandard() {
        userController.createUser(new User(0, "dolore", "NickName", "Nick Name", LocalDate.of(1995, 11, 28)));
        assertEquals("newEmail", userController.updateUser(new User(1, "newEmail", "NickName", "Nick Name", LocalDate.of(1995, 11, 28))).getEmail());
    }

    @Test
    @DisplayName("Обновление пользователя, Login содержит пробел")
    void updateExceptionUserName() {
        userController.createUser(new User(0, "dolore", "NickName", "Nick Name", LocalDate.of(1995, 11, 28)));
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
        userController.createUser(new User(0, "dolore", "NickName", "Nick Name", LocalDate.of(1995, 11, 28)));
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

    private Executable generateUpdateExecutableIDError() {
        return () -> userController.updateUser(new User(99, "fdhfgj", "StatusTask.NEW", "635", LocalDate.of(1995, 11, 28)));
    }


}