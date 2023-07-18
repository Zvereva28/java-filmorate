package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.storage.impl.dao.LikesDBStorage;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Sql({"/schema.sql", "/test-likes-data.sql"})
public class LikesDBStorageTest {
    private final LikesDBStorage likesDBStorage;

    @Test
    @DisplayName("Получение списка фильмов по id пользователя")
    void getFilm() {
        assertEquals(3, likesDBStorage.getFilmIdByUserId(1).size());
    }

    @Test
    @DisplayName("Получение списка пользователей по id фильма")
    void getUser() {
        assertEquals(2, likesDBStorage.getUserIdByFilmId(1).size());
    }
}
