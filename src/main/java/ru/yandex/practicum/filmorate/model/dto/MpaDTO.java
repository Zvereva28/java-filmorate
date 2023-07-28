package ru.yandex.practicum.filmorate.model.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.model.enums.RatingFilms;

@Data
@NoArgsConstructor
public class MpaDTO {
    private Integer id;

    private String name;

    public MpaDTO(Integer id) {
        this.id = id;
        this.name = RatingFilms.getById(id).getName();
    }
}
