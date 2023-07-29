package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Review {
    private Integer reviewId;

    private String content;

    private Boolean isPositive;

    private Integer userId;

    private Integer filmId;

    private Integer useful;
}
