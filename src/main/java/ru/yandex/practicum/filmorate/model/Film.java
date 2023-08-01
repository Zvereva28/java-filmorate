package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Film {
    private final List<Genres> genres = new ArrayList<>();

    private int id;

    @NotNull
    private String name;

    private String description;

    private LocalDate releaseDate;

    private Integer duration;

    private Mpa mpa = new Mpa();

    private Integer countLikes = 0;

    private final List<Director> directors = new ArrayList<>();

    public void addDirector(Director director) {
        directors.add(director);
    }
}