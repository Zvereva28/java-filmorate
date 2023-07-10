package ru.yandex.practicum.filmorate.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.UserException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
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
    private final UserValidator userValidator;

    public UserServiceImpl(@Qualifier("userDBStorage") UserStorage userStorage, UserValidator userValidator) {
        this.userStorage = userStorage;
        this.userValidator = userValidator;
    }

    @Override
    public User createUser(User user) {
        log.debug("+ createUser: {}", user);
        User newUser = userValidator.checkUser(user);
        log.debug("- createUser: {}", newUser);
        return userStorage.createUser(newUser);
    }

    @Override
    public User updateUser(User user) {
        log.debug("+ updateUser: {}", user);
        User newUser = userStorage.updateUser(userValidator.checkUser(user));
        log.debug("- updateUser: {}", newUser);
        return newUser;
    }

    @Override
    public List<User> getAllUsers() {
        List<User> users = userStorage.getAllUsers();
        log.debug("- allUsers: {}", users);
        return users;
    }

    @Override
    public User getUser(int id) {
        User user = userStorage.getUser(id);
        log.debug("- user: {}", user);
        return user;
    }

    @Override
    public void putFriend(int id, int friendId) {
        log.debug("+ addFriend : {}", id);
        if (id == friendId) {
            throw new UserException("Параметры не могут быть равны");
        }
        userStorage.userExist(id);
        userStorage.userExist(friendId);
        userStorage.addFriend(id, friendId);
    }

    @Override
    public void deleteFriend(int id, int friendId) {
        log.debug("+ deleteFriend : {}", id);
        if (id == friendId) {
            throw new UserException("Параметры не могут быть равны");
        }
        userStorage.userExist(id);
        userStorage.userExist(friendId);

        userStorage.deleteFriend(id, friendId);
    }

    @Override
    public List<User> getFriend(int id) {
        log.debug("+ getFriend : {}", id);
        Set<Integer> friendsId = userStorage.getUser(id).getFriends();
        List<User> friends = new ArrayList<>();
        for (Integer friendId : friendsId) {
            friends.add(userStorage.getUser(friendId));
        }
        log.debug("- getFriend : {}", friends);
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
}