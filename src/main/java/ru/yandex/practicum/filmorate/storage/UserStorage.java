package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {

    public User createUser(User newUser);

    public User updateUser(User newUser);

    public List<User> getAllUsers();

    public User getUser(int id);

    public void userExist(int id);

}