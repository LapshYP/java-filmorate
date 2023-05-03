package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.HashMap;

@Service
public interface GenreService {

    HashMap<Integer, Genre> getAllGenres();

    Genre getGenreById(int mpaId);
}
