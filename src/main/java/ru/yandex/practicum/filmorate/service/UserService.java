package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserService {

    User createUser(User newUser);

    User updateUser(User newUser);

    List<User> getAllUsers();

    User getUser(int id);

    void putFriend(int id, int friendId);

    void deleteFriend(int id, int friendId);

    List<User> getFriend(int id);

    List<User> getFriendsCommon(int id, int otherId);

}