package ru.yandex.practicum.filmorate.model;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

import javax.validation.constraints.NotBlank;

@Slf4j
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Director {

    private Integer id;
    @NotBlank(message = "Имя не может быть пустым")
    private String name;

    public Director(String name) {
        this.name = name;
    }
}
