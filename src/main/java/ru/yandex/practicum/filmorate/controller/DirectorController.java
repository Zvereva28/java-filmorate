package ru.yandex.practicum.filmorate.controller;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import java.util.List;

@Slf4j
@Getter
@RestController
@RequestMapping("/directors")
@RequiredArgsConstructor
public class DirectorController {

    private final DirectorService directorService;

    /**
     * Получаем всех режиссеров
     */
    @GetMapping()
    public List<Director> getAllDirectors() {
        return directorService.getAllDirectors();
    }

    /**
     * Получаем режиссера по id
     */
    @GetMapping("/{id}")
    public Director getDirectorById(@PathVariable int id) {
        return directorService.getDirector(id);
    }

    /**
     * Создаем режиссера
     */
    @PostMapping
    public Director createDirector(@Validated @RequestBody Director director) {
        return directorService.addDirector(director);
    }

    /**
     * Обновляем режиссера
     */
    @PutMapping
    public Director updateDirector(@Validated @RequestBody Director director) {
        return directorService.updateDirector(director);
    }

    /**
     * Удаляем режиссера
     */
    @DeleteMapping("/{id}")
    public void deleteDirector(@PathVariable int id) {
        directorService.deleteDirector(id);
    }
}
