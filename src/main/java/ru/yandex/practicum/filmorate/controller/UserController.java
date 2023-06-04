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
        var usersList = new ArrayList<>(users.values());
        log.debug("+ allUsers: {}", usersList);
        return usersList;
    }

    @PostMapping
    public User postUser(@RequestBody @Validated User user) {
        log.debug("+ createUser: {}", user);
        User newUser = userValidator.checkUser(user);
        newUser.setId(generateId());
        users.put(newUser.getId(), newUser);
        log.debug("- createUser: {}", newUser);
        return newUser;
    }

    @PutMapping
    public User putUser(@RequestBody @Validated User user) {
        log.debug("+ updateUser: {}", user);
        if (!users.containsKey(user.getId())) {
            throw new UserException("Пользователя с id = " + user.getId() + " не существует");
        }
        User newUser = userValidator.checkUser(user);
        User oldUser = users.get(newUser.getId());
        oldUser.setName(newUser.getName());
        oldUser.setBirthday(newUser.getBirthday());
        oldUser.setLogin(newUser.getLogin());
        oldUser.setEmail(newUser.getEmail());
        log.debug("- updateUser: {}", oldUser);
        return oldUser;
    }

    private Integer generateId() {
        idManager++;
        return idManager;
    }
}
