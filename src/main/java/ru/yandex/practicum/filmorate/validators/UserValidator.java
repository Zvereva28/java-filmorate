package ru.yandex.practicum.filmorate.validators;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.userExceptions.UserException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Objects;

@Slf4j
@Component
public class UserValidator {

    public User checkUser(User user) {

        if (user.getLogin().contains(" ")) {
            log.info("Login не должен содержать пробелы");
            throw new UserException("Login не должен содержать пробелы");
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.info("Не верная дата рождения");
            throw new UserException("Не верная дата рождения");
        }

        if (!Objects.nonNull(user.getName()) || user.getName().isEmpty() || user.getName().isBlank()) {
            log.info("Name не должен быть пустым ");
            user.setName(user.getLogin());
        }
        return user;
    }
}