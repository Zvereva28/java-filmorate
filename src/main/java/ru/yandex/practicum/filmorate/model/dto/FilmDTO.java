package ru.yandex.practicum.filmorate.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Genres;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.validationAnnotations.InMovieEpoch;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FilmDTO {
    private final List<Genres> genres = new ArrayList<>();

    private int id;

    @NotNull
    @NotEmpty(message = "Название фильма не может быть пустым")
    private String name;

    @Size(max = 200, message = "Описание не может быть больше 200 символов")
    private String description;

    @InMovieEpoch(message = "Ошибка в дате выхода фильма")
    private LocalDate releaseDate;

    @Positive(message = "Продолжительность фильма не может быть отрицательной")
    private Integer duration;

    private Mpa mpa = new Mpa();

    private Integer countLikes = 0;

    private final List<Director> directors = new ArrayList<>();
}
