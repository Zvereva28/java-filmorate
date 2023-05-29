package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.UserException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.validators.UserValidator;

import java.util.*;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private UserValidator userValidator = new UserValidator();
    private Map<Integer, User> users = new HashMap<>();
    private int idManager = 0;


    @GetMapping
    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }

    @PostMapping
    public User postUser(@RequestBody @Validated User user) {
        userValidator.checkUser(user);
        if (!Objects.nonNull(user.getName()) || user.getName().isEmpty() || user.getName().isBlank()) {
            log.debug("Name не должен быть пустым ");
            user.setName(user.getLogin());
        }
        user.setId(generateId());
        users.put(user.getId(), user);
        return user;
    }

    @PutMapping
    public User putUser(@RequestBody @Validated User user) {
        if (!users.containsKey(user.getId())) {
            throw new UserException("Пользователя с id = " + user.getId() + " не существует");
        }
        userValidator.checkUser(user);
        users.put(user.getId(), user);
        return user;
    }

    private Integer generateId() {
        idManager++;
        return idManager;
    }
}
