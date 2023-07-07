package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

public interface MpaService {

    public Mpa getMpaById(int id);

    public List<Mpa> getAll();

}