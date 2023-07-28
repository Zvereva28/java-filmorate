package ru.yandex.practicum.filmorate.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.userExceptions.UserException;
import ru.yandex.practicum.filmorate.model.FeedEvent;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.enums.FeedEventType;
import ru.yandex.practicum.filmorate.model.enums.FeedOperation;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.FeedStorage;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

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

    public UserServiceImpl(UserStorage userStorage, FilmStorage filmStorage, FeedStorage feedStorage) {
        this.userStorage = userStorage;
        this.feedStorage = feedStorage;
        this.filmStorage = filmStorage;
    }

    @Override
    public User addUser(User user) {
        log.info("+ createUser : {}", user);
        checkEmptyName(user);
        User answer = userStorage.createUser(user);
        log.info("- createUser : {}", answer);
        return answer;
    }

    @Override
    public User updateUser(User user) {
        log.info("+ updateUser : {}", user);
        checkEmptyName(user);
        User newUser = userStorage.updateUser(user);
        log.info("- updateUser : {}", newUser);
        return newUser;
    }

    @Override
    public List<User> getAllUsers() {
        log.info("+ updateUser");
        List<User> users = userStorage.getAllUsers();
        log.info("- allUsers : {}", users);
        return users;
    }

    @Override
    public User getUser(int id) {
        log.info("+ getUser : id = {}", id);
        User user = userStorage.getUser(id);
        log.info("- getUser : {}", user);
        return user;
    }

    @Override
    public void addFriend(int id, int friendId) {
        log.info("+ addFriend : id = {}, friendId = {}", id, friendId);
        if (id == friendId) {
            throw new UserException("Параметры не могут быть равны");
        }
        userStorage.userExist(id);
        userStorage.userExist(friendId);
        userStorage.addFriend(id, friendId);
        feedStorage.addToFeedDb(id, FeedEventType.FRIEND, FeedOperation.ADD, friendId);
        log.info("- addFriend : id = {}, friendId = {}", id, friendId);
    }

    @Override
    public void deleteFriend(int id, int friendId) {
        log.info("+ deleteFriend : id = {}, friendId = {}", id, friendId);
        if (id == friendId) {
            throw new UserException("Параметры не могут быть равны");
        }
        userStorage.userExist(id);
        userStorage.userExist(friendId);
        userStorage.deleteFriend(id, friendId);
        feedStorage.addToFeedDb(id, FeedEventType.FRIEND, FeedOperation.REMOVE, friendId);
        log.info("- deleteFriend : id = {}, friendId = {}", id, friendId);
    }

    @Override
    public List<User> getFriends(int id) {
        log.info("+ getFriends : id = {}", id);
        Set<Integer> friendsId = userStorage.getUser(id).getFriends();
        List<User> friends = new ArrayList<>();
        for (Integer friendId : friendsId) {
            friends.add(userStorage.getUser(friendId));
        }
        log.info("- getFriends : {}", friends);
        return friends;
    }

    @Override
    public List<User> getFriendsCommon(int id, int otherId) {
        log.info("+ getFriendsCommon : {} {}", id, otherId);
        Set<Integer> friendsId = userStorage.getUser(id).getFriends();
        Set<Integer> otherFriends = userStorage.getUser(otherId).getFriends();
        Set<Integer> common = friendsId.stream()
                .filter(otherFriends::contains)
                .collect(Collectors.toSet());
        List<User> friends = new ArrayList<>();
        for (Integer friendId : common) {
            friends.add(userStorage.getUser(friendId));
        }
        log.info("- getFriendsCommon : {}", friends);
        return friends;
    }

    @Override
    public List<Film> getRecommendations(int userId) {
        log.info("+ getRecommendations : userId = {}", userId);
        List<Film> answer = filmStorage.getRecommendations(userId);
        log.info("- getRecommendations : {}", answer);
        return answer;
    }

    @Override
    public List<FeedEvent> getFeed(int id) {
        log.info("+ getFeed : id = {}", id);
        userStorage.getUser(id);
        List<FeedEvent> answer = feedStorage.getFeedByUserId(id);
        log.info("- getFeed : {}", answer);
        return answer;
    }

    @Override
    public void deleteUser(int id) {
        log.info("+ deleteUser : id = {}", id);
        userStorage.deleteUser(id);
        log.info("- deleteUser : id = {}", id);
    }

    private void checkEmptyName(User user) {
        log.info("Начата проверка имени пользователя с логином {}", user.getLogin());
        if (user.getName().isEmpty()) {
            user.setName(user.getLogin());
            log.info("Пользователю {} присвоен логин как имя", user.getLogin());
        }
        log.info("Пользователь {} прошел проверку имени: {}", user.getName(), user);
    }
}