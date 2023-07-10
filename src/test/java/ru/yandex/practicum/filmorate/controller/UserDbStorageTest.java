package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.exception.UserException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.impl.dao.UserDBStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class UserDbStorageTest {

    private final UserDBStorage userStorage;


    @Test
    @DisplayName("Список пользователей, когда он пуст")
    void findAllNullArray() {
        System.out.println("userController.getUsers().size() = " + userStorage.getAllUsers().size());
        assertEquals(0, userStorage.getAllUsers().size());
    }

    @Test
    @DisplayName("Список пользователей")
    void findAllStandard() {
        userStorage.createUser(new User(0, "dolore", "NickName", "Nick Name", LocalDate.of(1995, 11, 28)));
        userStorage.createUser(new User(0, "dolore2", "LogName", "NickName", LocalDate.of(1985, 11, 28)));
        assertEquals(2, userStorage.getAllUsers().size());
    }

    @Test
    @DisplayName("Создание пользователя")
    void createStandard() {
        assertEquals(1, userStorage.createUser(new User(0, "dolore", "NickName", "Nick Name", LocalDate.now())).getId());
    }

    @Test
    @DisplayName("Проверка существования пользователя")
    void userExistStandard() {
        userStorage.createUser(new User(0, "dolore", "NickName", "Nick Name", LocalDate.now()));
        assertEquals(1, userStorage.userExist(1).getId());
    }

    @Test
    @DisplayName("Проверка не существующего пользователя")
    void userExistIDError() {
        UserException exception = assertThrows(
                UserException.class,
                generateUserExistExecutableIDError()
        );
        assertEquals("Пользователя с id = 99 не существует", exception.getMessage());
    }

    private Executable generateUserExistExecutableIDError() {
        return () -> userStorage.userExist(99);
    }

    @Test
    @DisplayName("Обновление пользователя")
    void updateStandard() {
        userStorage.createUser(new User(0, "dolore", "NickName", "Nick Name", LocalDate.of(1995, 11, 28)));
        assertEquals("newEmail", userStorage.updateUser(new User(1, "newEmail", "NickName", "Nick Name", LocalDate.of(1995, 11, 28))).getEmail());
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
        return () -> userStorage.updateUser(new User(99, "fdhfgj", "StatusTask.NEW", "635", LocalDate.of(1995, 11, 28)));
    }
}
