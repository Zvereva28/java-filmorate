package ru.yandex.practicum.filmorate.model;

import lombok.*;
import org.springframework.lang.NonNull;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Film {

    private final List<Genres> genres = new ArrayList<>();
    private int id;
    @NonNull
    private String name;
    private String description;
    private LocalDate releaseDate;
    private Integer duration;
    private Mpa mpa = new Mpa();
    private Integer countLikes = 0;

}