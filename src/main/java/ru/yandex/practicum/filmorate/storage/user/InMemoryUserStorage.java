package ru.yandex.practicum.filmorate.storage.user;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@AllArgsConstructor
@NoArgsConstructor
public class InMemoryUserStorage implements UserStorage {
    private Map<Integer, User> users = new HashMap<>();
    private int idManager;

    @Override
    public User createUser(User user) {
        user.setId(generateId());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public void userExist(int id) {
        if (!users.containsKey(id)) {
            throw new UserNotFoundException("Пользователя с id = " + id + " не существует");
        }
    }

    @Override
    public User updateUser(User user) {
        userExist(user.getId());
        User oldUser = users.get(user.getId());
        oldUser.setName(user.getName());
        oldUser.setBirthday(user.getBirthday());
        oldUser.setLogin(user.getLogin());
        oldUser.setEmail(user.getEmail());
        return oldUser;
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User getUser(int id) {
        userExist(id);
        return users.get(id);
    }

    private Integer generateId() {
        idManager++;
        return idManager;
    }
}
