package ru.yandex.practicum.filmorate.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.validationAnnotations.NoSpaces;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsersDTO {
    private final Set<Integer> friends = new HashSet<>();

    private int id;

    @NotNull
    @Email
    private String email;

    @NotBlank(message = "Логин не может быть пустым")
    @NoSpaces(message = "Логин не может содержать пробелы")
    private String login;

    private String name;

    @Past(message = "Ошибка в дате рождения")
    private LocalDate birthday;
}
