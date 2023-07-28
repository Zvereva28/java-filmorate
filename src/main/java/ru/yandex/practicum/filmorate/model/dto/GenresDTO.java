package ru.yandex.practicum.filmorate.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GenresDTO {
    private int id;

    private String name;
}
