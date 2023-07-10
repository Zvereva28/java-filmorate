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
    public Mpa getMpaById(int id) {
        return new Mpa(id);
    }

    @Override
    public List<Mpa> getAll() {
        RatingFilms[] ratingFilms = RatingFilms.values();
        List<Mpa> mpaRatings = new ArrayList<>();
        for (RatingFilms rating : ratingFilms) {
            mpaRatings.add(new Mpa(rating.getId()));
        }
        return mpaRatings;
    }
}