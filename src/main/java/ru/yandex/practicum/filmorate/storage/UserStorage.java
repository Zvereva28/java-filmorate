package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {

    User createUser(User newUser);

    User updateUser(User newUser);

    List<User> getAllUsers();

    User getUser(int id);

    User userExist(int id);

    void addFriend(int id, int friendId);

    void deleteFriend(int id, int friendId);

}