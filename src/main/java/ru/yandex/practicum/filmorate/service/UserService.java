package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.FeedEvent;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserService {

    User createUser(User newUser);

    User updateUser(User newUser);

    List<User> getAllUsers();

    User getUser(int id);

    void putFriend(int id, int friendId);

    void deleteFriend(int id, int friendId);

    void deleteUserById(int id);

    List<User> getFriend(int id);

    List<User> getFriendsCommon(int id, int otherId);

    List<Film> getRecommendations(int userId);

    List<FeedEvent> getFeedByUserId(int id);
}