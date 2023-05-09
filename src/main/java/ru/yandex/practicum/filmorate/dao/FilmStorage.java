package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.HashMap;

public interface FilmStorage {
    Film addFilmToRepo(Film film);

    Film getFilmFromRepo(Film film);

    HashMap<Integer, Film> getFilmsFromRepo();

    Film updateFilm(Film film);

    Film getFilmFromRepoById(int filmId);

    Film addFilmLikeToRepo(Film filmToLike, int userId);

    HashMap<Integer, Film> getFilmsFromRepoWithCount(int count);

    int removeLikeFromRepo(int filmId, int userId);
}
