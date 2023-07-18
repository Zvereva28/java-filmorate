package ru.yandex.practicum.filmorate.storage;

import java.util.List;

public interface LikesStorage {
    List<Integer> getFilmIdByUserId(int userId);

    List<Integer> getUserIdByFilmId(int userId);
}
