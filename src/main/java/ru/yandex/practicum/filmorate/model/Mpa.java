package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.model.enums.RatingFilms;

@Data
@NoArgsConstructor
public class Mpa {
    private Integer id;
    private String name;

    public Mpa(RatingFilms ratingFilms) {
        this.id = ratingFilms.getId();
        this.name = ratingFilms.getName();
    }

    public Mpa(Integer id) {
        this.id = id;
        this.name = RatingFilms.getById(id).getName();
    }

}
