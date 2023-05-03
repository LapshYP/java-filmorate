package ru.yandex.practicum.filmorate.dao.impl;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.FilmStorage;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.HashMap;

@Repository
public class FilmStorageImpl implements FilmStorage {

    private final HashMap<Integer, Film> filmStore = new HashMap<>();

    @Override
    public Film addFilmToRepo(Film film) {
        filmStore.put(film.getId(), film);
        return film;
    }

    @Override
    public Film getFilmFromRepo(Film film) {
        Film filmToUpdate = filmStore.get(film.getId());
        return filmToUpdate;
    }

    @Override
    public HashMap<Integer, Film> getFilmsFromRepo() {
        return filmStore;
    }


    public Film getFilmFromRepoById(int filmId) {
        Film film = filmStore.get(filmId);
        if (film == null) {
            throw new NotFoundException(HttpStatus.NOT_FOUND, "Film with this ID don't found");
        }
        return film;
    }

    @Override
    public Film addFilmLikeToRepo(Film filmToLike, int userId) {
        return null;
    }

    @Override
    public int removeLikeFromRepo(int filmId, int userId) {
        return 0;
    }

    @Override
    public Film updateFilm(Film film) {
        return null;
    }
}
