package ru.yandex.practicum.filmorate.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.UserException;
import ru.yandex.practicum.filmorate.model.FeedEvent;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.enums.FeedEventType;
import ru.yandex.practicum.filmorate.model.enums.FeedOperation;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.FeedStorage;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.validators.UserValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;
    private final FeedStorage feedStorage;
    private final UserValidator userValidator;

    public UserServiceImpl(UserStorage userStorage, FilmStorage filmStorage, FeedStorage feedStorage,
                           UserValidator userValidator) {
        this.userStorage = userStorage;
        this.feedStorage = feedStorage;
        this.userValidator = userValidator;
        this.filmStorage = filmStorage;
    }

    @Override
    public User addUser(User user) {
        log.debug("+ createUser : {}", user);
        User newUser = userValidator.checkUser(user);
        User answer = userStorage.createUser(newUser);
        log.debug("- createUser : {}", answer);
        return answer;
    }

    @Override
    public User updateUser(User user) {
        log.debug("+ updateUser : {}", user);
        User newUser = userStorage.updateUser(userValidator.checkUser(user));
        log.debug("- updateUser : {}", newUser);
        return newUser;
    }

    @Override
    public List<User> getAllUsers() {
        log.debug("+ updateUser");
        List<User> users = userStorage.getAllUsers();
        log.debug("- allUsers : {}", users);
        return users;
    }

    @Override
    public User getUser(int id) {
        log.debug("+ getUser : id = {}", id);
        User user = userStorage.getUser(id);
        log.debug("- getUser : {}", user);
        return user;
    }

    @Override
    public void addFriend(int id, int friendId) {
        log.debug("+ addFriend : id = {}, friendId = {}", id, friendId);
        if (id == friendId) {
            throw new UserException("Параметры не могут быть равны");
        }
        userStorage.getUser(id);
        userStorage.getUser(friendId);
        userStorage.addFriend(id, friendId);
        feedStorage.addToFeedDb(id, FeedEventType.FRIEND, FeedOperation.ADD, friendId);
        log.debug("- addFriend : id = {}, friendId = {}", id, friendId);
    }

    @Override
    public void deleteFriend(int id, int friendId) {
        log.debug("+ deleteFriend : id = {}, friendId = {}", id, friendId);
        if (id == friendId) {
            throw new UserException("Параметры не могут быть равны");
        }
        userStorage.getUser(id);
        userStorage.getUser(friendId);
        userStorage.deleteFriend(id, friendId);
        feedStorage.addToFeedDb(id, FeedEventType.FRIEND, FeedOperation.REMOVE, friendId);
        log.debug("- deleteFriend : id = {}, friendId = {}", id, friendId);
    }

    @Override
    public List<User> getFriends(int id) {
        log.debug("+ getFriends : id = {}", id);
        Set<Integer> friendsId = userStorage.getUser(id).getFriends();
        List<User> friends = new ArrayList<>();
        for (Integer friendId : friendsId) {
            friends.add(userStorage.getUser(friendId));
        }
        log.debug("- getFriends : {}", friends);
        return friends;
    }

    @Override
    public List<User> getFriendsCommon(int id, int otherId) {
        log.debug("+ getFriendsCommon : {} {}", id, otherId);
        Set<Integer> friendsId = userStorage.getUser(id).getFriends();
        Set<Integer> otherFriends = userStorage.getUser(otherId).getFriends();
        Set<Integer> common = friendsId.stream()
                .filter(otherFriends::contains)
                .collect(Collectors.toSet());
        List<User> friends = new ArrayList<>();
        for (Integer friendId : common) {
            friends.add(userStorage.getUser(friendId));
        }
        log.debug("- getFriendsCommon : {}", friends);
        return friends;
    }

    @Override
    public List<Film> getRecommendations(int userId) {
        log.debug("+ getRecommendations : userId = {}", userId);
        List<Film> films = filmStorage.getRecommendations(userId);
        log.debug("- getRecommendations : {}", films);
        return films;
    }

    @Override
    public List<FeedEvent> getFeed(int id) {
        log.debug("+ getFeed : id = {}", id);
        userStorage.getUser(id);
        List<FeedEvent> events = feedStorage.getFeedByUserId(id);
        log.debug("- getFeed : {}", events);
        return events;
    }

    @Override
    public void deleteUser(int id) {
        log.debug("+ deleteUser : id = {}", id);
        userStorage.deleteUser(id);
        log.debug("- deleteUser : id = {}", id);
    }
}