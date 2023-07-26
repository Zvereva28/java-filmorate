package ru.yandex.practicum.filmorate.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.enums.RatingFilms;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class MpaServiceImpl implements MpaService {
    @Override
    public Mpa getMpa(int id) {
        log.info("+ getMpa : id = {}", id);
        Mpa answer = new Mpa(id);
        log.info("- getMpa : {}", answer);
        return answer;
    }

    @Override
    public List<Mpa> getAllMpa() {
        log.info("+ getAllMpa");
        RatingFilms[] ratingFilms = RatingFilms.values();
        List<Mpa> mpaRatings = new ArrayList<>();
        for (RatingFilms rating : ratingFilms) {
            mpaRatings.add(new Mpa(rating.getId()));
        }
        log.info("- getAllMpa : {}", mpaRatings);
        return mpaRatings;
    }
}