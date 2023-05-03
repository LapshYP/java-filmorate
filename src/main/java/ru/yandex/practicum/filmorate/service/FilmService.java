package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

@Service
public interface FilmService {
    Film getFilmById(int filmId);

    Film updateFilm(@Valid Film film);

    Film addFilms(@Valid Film film);

    HashMap<Integer, Film> getAllFilms();

    Film addLike(int filmId, int userId);

    Film removeLike(int filmId, int userId);

    Set<Film> getPopularFilms(int count);

}
