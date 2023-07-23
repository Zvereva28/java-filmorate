package ru.yandex.practicum.filmorate.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.exception.MpaNotFoundException;

import java.util.Arrays;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public enum RatingFilms {
    G("G", 1),
    PG("PG", 2),
    PG_13("PG-13", 3),
    R("R", 4),
    NC_17("NC-17", 5);
    private String name;
    private Integer id;

    public static RatingFilms getById(int id) {
        return Arrays.stream(RatingFilms.values())
                .filter(value -> id == (value.id)).findAny()
                .orElseThrow(() -> new MpaNotFoundException(String.format("id = %s не найден", id)));
    }

}