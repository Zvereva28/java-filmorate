package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
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
}
