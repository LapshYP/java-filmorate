package ru.yandex.practicum.filmorate.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.yandex.practicum.filmorate.dao.FilmStorage;
import ru.yandex.practicum.filmorate.dao.UserStorage;
import ru.yandex.practicum.filmorate.dao.impl.FilmDbStorageImpl;
import ru.yandex.practicum.filmorate.dao.impl.UserDbStorageImpl;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service("filmDbService")
@Validated
@Primary
@RequiredArgsConstructor
public class FilmDbServiceImpl implements FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private static int id = 1;

    @Autowired
    public FilmDbServiceImpl(FilmDbStorageImpl filmStorage, UserDbStorageImpl userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    @Override
    public Film getFilmById(int filmId) {
        return filmStorage.getFilmFromRepoById(filmId);

    }

    @Override
    public @Valid Film updateFilm(@Valid Film film) {
        Film filmToUpdate = filmStorage.getFilmFromRepo(film);
        if (filmToUpdate == null) {
            log.error("Film with name = \"{}\" don't find", film.getName());
            throw new NotFoundException(HttpStatus.NOT_FOUND, "Film with name = \"" + film.getName() + "\" don't find");
        } else {
            filmStorage.updateFilm(film);
            return film;
        }
    }

    @Override
    public @Valid Film addFilms(@Valid Film film) {
        if (film.getId() == 0) {
            film.setId(id++);
        }
        filmStorage.addFilmToRepo(film);
        return film;
    }

    @Override
    public HashMap<Integer, Film> getAllFilms() {
        return filmStorage.getFilmsFromRepo();
    }

    @Override
    public Film addLike(int filmId, int userId) {
        Film filmToLike = filmStorage.getFilmFromRepoById(filmId);
        Set<Integer> likes = filmToLike.getLikes();
        likes.add(userId);
        filmStorage.addFilmLikeToRepo(filmToLike, userId);
        return filmToLike;

    }

    @Override
    public Film removeLike(int filmId, int userId) {
        if (!userStorage.getUsersFromRepo().containsKey(userId)) {
            log.error("a user with such an ID  = \"{}\" is not located", userId);
            throw new NotFoundException(HttpStatus.NOT_FOUND, "a user with such an ID = \"" + userId + "\" is not located");
        } else {
            int removeLikeFromRepo = filmStorage.removeLikeFromRepo(filmId, userId);
            System.out.println(removeLikeFromRepo);
            return null;
        }

    }

    @Override
    public Set<Film> getPopularFilms(int count) {
        return filmStorage.getFilmsFromRepoWithCount(count).values().stream()

                .collect(Collectors.toSet());
    }

}
