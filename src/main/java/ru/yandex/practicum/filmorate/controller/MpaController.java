package ru.yandex.practicum.filmorate.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/mpa")
@AllArgsConstructor
public class MpaController {
    private final MpaService mpaService;

    @GetMapping("/{id}")
    public Mpa getMpaById(@PathVariable int id) {
        return mpaService.getMpaById(id);
    }

    @GetMapping
    public List<Mpa> getAllMpa() {
        return mpaService.getAll();
    }
}
