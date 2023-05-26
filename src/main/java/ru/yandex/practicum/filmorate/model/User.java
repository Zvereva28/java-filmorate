package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.lang.NonNull;

import javax.validation.constraints.Email;
import java.time.LocalDate;

@Data
@AllArgsConstructor
public class User {
    private int id;
    @NonNull
    @Email
    private String email;
    @NonNull
    private String login;
    private String name;
    private LocalDate birthday;


}
