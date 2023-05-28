package ru.yandex.practicum.filmorate.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.yandex.practicum.filmorate.dao.GenreStorage;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.util.HashMap;

@Slf4j
@Service
@Validated
@Primary
@RequiredArgsConstructor
public class GenreDbServiceImpl implements GenreService {

    private final GenreStorage genreStorage;

    @Override
    public HashMap<Integer, Genre> getAllGenres() {
        return genreStorage.getGenresFromRepo();
    }

    @Override
    public Genre getGenreById(int mpaId) {
        return genreStorage.getGenreFromRepoById(mpaId);
    }


}
