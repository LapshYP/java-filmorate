package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.HashMap;

public interface GenreStorage {


    Genre getGenreFromRepoById(int genreId);

    HashMap<Integer, Genre> getGenresFromRepo();
}
