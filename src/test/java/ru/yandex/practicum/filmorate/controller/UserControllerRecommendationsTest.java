package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Sql({"/schema.sql", "/test-recommendations-data.sql"})
public class UserControllerRecommendationsTest {
    private final UserController userController;

    @Test
    void getRecommendations() {
        assertEquals(3, userController.getRecommendations(1).size());
    }

    @Test
    void getRecommendationsWhenFilm() {
        assertEquals(4, userController.getRecommendations(1).get(0).getId());
        assertEquals(5, userController.getRecommendations(1).get(1).getId());
        assertEquals(3, userController.getRecommendations(1).get(2).getId());
    }
}
