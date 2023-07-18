package ru.yandex.practicum.filmorate.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.RecommendationsService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.LikesStorage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class RecommendationsServiceImpl implements RecommendationsService {
    private final FilmStorage filmStorage;
    private final LikesStorage likesStorage;

    public RecommendationsServiceImpl(@Qualifier("filmDBStorage")FilmStorage filmStorage, LikesStorage likesStorage) {
        this.filmStorage = filmStorage;
        this.likesStorage = likesStorage;
    }

    @Override
    public List<Film> getRecommendations(int userId) {
        log.info("get recommendations for user {}", userId);
        List<Integer> likedFilms = likesStorage.getFilmIdByUserId(userId);
        log.info("liked films for user {} is {}", userId, likedFilms);
        Set<Integer> users = new HashSet<>();
        Set<Integer> films = new HashSet<>();
        List<Film> answer = new ArrayList<>();

        if (likedFilms.size() > 0) {
            for (int filmId : likedFilms) {
                users.addAll(likesStorage.getUserIdByFilmId(filmId));
            }
            log.info("users who liked same as user {} is {} ", userId, users);

            if (users.size() != 0) {
                for (int user : users) {
                    films.addAll(likesStorage.getFilmIdByUserId(user));
                }
                log.info("films that users who liked same as user {} is {}", userId, films);
            }

            likedFilms.forEach(films::remove);
            for (int filmId : films) {
                answer.add(filmStorage.getFilm(filmId));
            }
            log.info("recommended films for user {} is {}", userId, answer);
        }

        return answer;
    }
}
