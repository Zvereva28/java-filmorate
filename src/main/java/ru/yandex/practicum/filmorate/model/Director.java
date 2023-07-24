package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

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
