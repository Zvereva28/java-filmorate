package ru.yandex.practicum.filmorate.storage.impl.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component("userDBStorage")
public class UserDBStorage implements UserStorage {
    private static final String SELECT_USER = "SELECT id, user_name, email, login, birthday, f.friend_id as friends " +
            "FROM users as u LEFT JOIN friends as f ON u.id=f.user_id WHERE id = ?";
    private static final String DELETE_FRIENDS = "DELETE FROM friends WHERE user_id = ? AND friend_id = ?";
    private static final String UPDATE_USER = "UPDATE users set user_name=?, email=?, login=?, birthday=? WHERE id=?";
    private static final String SELECT_ALL_USER = "SELECT id, user_name, email, login, birthday, f.friend_id as friends " +
            "FROM users as u LEFT JOIN friends as f ON u.id=f.user_id ORDER BY id";
    private final JdbcTemplate jdbcTemplate;

    public UserDBStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User createUser(User user) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate.getDataSource())
                .withTableName("users")
                .usingGeneratedKeyColumns("id");
        Map<String, String> params = Map.of("user_name", user.getName(), "email", user.getEmail(),
                "login", user.getLogin(), "birthday", user.getBirthday().toString());
        Number id = simpleJdbcInsert.executeAndReturnKey(params);
        user.setId(id.intValue());

        return user;
    }

    @Override
    public User userExist(int id) {
        return jdbcTemplate.query(SELECT_USER, userRowMapper(), id).stream()
                .findFirst().orElseThrow(() -> new UserNotFoundException("Пользователя с id = " + id + " не существует"));

    }

    @Override
    public void addFriend(int id, int friendId) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate.getDataSource())
                .withTableName("friends");
        Map<String, Integer> params = Map.of("user_id", id, "friend_id", friendId);
        simpleJdbcInsert.execute(params);
    }


    public void deleteFriend(int id, int friendId) {
        jdbcTemplate.update(DELETE_FRIENDS,
                id, friendId);
    }

    @Override
    public User updateUser(User user) {
        userExist(user.getId());
        jdbcTemplate.update(UPDATE_USER,
                user.getName(), user.getEmail(), user.getLogin(), user.getBirthday(), user.getId());

        return user;
    }

    @Override
    public List<User> getAllUsers() {
        return jdbcTemplate.query(SELECT_ALL_USER, usersRowMapper()).stream().findFirst().orElse(new ArrayList<>());
    }

    @Override
    public User getUser(int id) {
        return userExist(id);
    }

    private RowMapper<User> userRowMapper() {
        return (rs, rowNum) -> {
            User user = getColumns(rs);
            do {
                if (rs.getInt("friends") > 0) {
                    user.getFriends().add(rs.getInt("friends"));
                }
            } while (rs.next());

            return user;
        };
    }

    private RowMapper<List<User>> usersRowMapper() {
        return (rs, rowNum) -> {
            List<User> users = new ArrayList<>();
            User user = getUserFromBD(rs);
            while (rs.next()) {
                if (user.getId() != rs.getInt("id")) {
                    users.add(user);
                    user = getUserFromBD(rs);
                }
                if (rs.getInt("friends") > 0) {
                    user.getFriends().add(rs.getInt("friends"));
                }
            }
            users.add(user);

            return users;
        };
    }

    private User getUserFromBD(ResultSet rs) throws SQLException {
        User user = getColumns(rs);
        if (rs.getInt("friends") > 0) {
            user.getFriends().add(rs.getInt("friends"));
        }
        return user;
    }

    private User getColumns(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setEmail(rs.getString("email"));
        user.setLogin(rs.getString("login"));
        user.setName(rs.getString("user_name"));
        user.setBirthday(rs.getDate("birthday").toLocalDate());

        return user;
    }
}
