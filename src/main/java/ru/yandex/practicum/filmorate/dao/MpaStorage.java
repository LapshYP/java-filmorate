package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.MPA;

import java.util.HashMap;

public interface MpaStorage {

    HashMap<Integer, MPA> getMpasFromRepo();

    MPA getMpaFromRepoById(int mpaId);

}
