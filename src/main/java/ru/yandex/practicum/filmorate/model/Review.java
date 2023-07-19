package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Review {
    private Integer reviewId;

    @NotNull(message = "content may not be null")
    private String content;

    @NotNull(message = "type may not be null")
    private Boolean isPositive;

    @NotNull(message = "userId may not be null")
    private Integer userId;

    @NotNull(message = "filmId may not be null")
    private Integer filmId;

    private Integer useful;


}
