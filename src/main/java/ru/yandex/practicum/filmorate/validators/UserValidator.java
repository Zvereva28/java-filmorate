package ru.yandex.practicum.filmorate.validators;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exception.FilmException;
import ru.yandex.practicum.filmorate.exception.UserException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

@Slf4j
public class UserValidator {

    public static void checkUser(User user) {

        if (user.getLogin().contains(" ")) {
            log.debug("Login не должен содержать пробелы");
            throw new UserException("Login не должен содержать пробелы");
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.debug("Не верная дата рождения");
            throw new UserException("Не верная дата рождения");
        }

    }
}
