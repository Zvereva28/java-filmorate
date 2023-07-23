package ru.yandex.practicum.filmorate.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.FeedEvent;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
@AllArgsConstructor
public class UserController {
    private UserService userService;

    @GetMapping
    public List<User> getUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable int id) {
        return userService.getUser(id);
    }

    @PostMapping
    public User postUser(@RequestBody @Validated User user) {
        return userService.createUser(user);
    }

    @PutMapping
    public User putUser(@RequestBody @Validated User user) {
        return userService.updateUser(user);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void putFriends(@PathVariable int id, @PathVariable int friendId) {
        userService.putFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriends(@PathVariable int id, @PathVariable int friendId) {
        userService.deleteFriend(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public List<User> getFriends(@PathVariable int id) {
        return userService.getFriend(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getFriendsCommon(@PathVariable int id, @PathVariable int otherId) {
        return userService.getFriendsCommon(id, otherId);
    }

    @GetMapping("/{id}/recommendations")
    public List<Film> getRecommendations(@PathVariable int id) {
        return userService.getRecommendations(id);
    }

    @GetMapping("/{id}/feed")
    public List<FeedEvent> getFeedByUserId(@PathVariable int id) {
        return userService.getFeedByUserId(id);
    }
}
