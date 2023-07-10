package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.NonNull;

import javax.validation.constraints.Email;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private final Set<Integer> friends = new HashSet<>();
    private int id;
    @NonNull
    @Email
    private String email;
    @NonNull
    private String login;
    private String name;
    private LocalDate birthday;
}