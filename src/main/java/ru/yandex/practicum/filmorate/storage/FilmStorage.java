package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.HashMap;

public interface FilmStorage {
    Film addFilmToRepo (Film film);
    Film getFilmFromRepo(Film film);
    HashMap<Integer, Film> getFilmsFromRepo();
}
