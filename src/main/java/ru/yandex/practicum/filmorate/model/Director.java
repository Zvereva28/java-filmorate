package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.validation.constraints.NotBlank;

@Slf4j
@Data
@NoArgsConstructor
public class Director {

    private int id;
    @NotBlank(message = "Имя не может быть пустым")
    private String name;
}
