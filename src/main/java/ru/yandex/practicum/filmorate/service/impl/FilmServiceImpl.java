package ru.yandex.practicum.filmorate.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.yandex.practicum.filmorate.dao.impl.FilmStorageImpl;
import ru.yandex.practicum.filmorate.dao.impl.UserStorageImpl;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@Validated
public class FilmServiceImpl implements FilmService {
    private final FilmStorageImpl filmStorage;
    private final UserStorageImpl userStorage;

    @Autowired
    public FilmServiceImpl(FilmStorageImpl filmStorage, UserStorageImpl userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    private static int id = 1;

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
            filmStorage.addFilmToRepo(film);
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
        Film filmToUpdate = filmStorage.getFilmFromRepoById(filmId);
        Set<Integer> likes = filmToUpdate.getLikes();
        likes.add(userId);
//       filmToUpdate.setLikes(likes);
        return filmToUpdate;
    }

    @Override
    public Film removeLike(int filmId, int userId) {
        Film filmToUpdate = filmStorage.getFilmFromRepoById(filmId);
        if (!userStorage.getUsersFromRepo().containsKey(userId)) {
            log.error("a user with such an ID  = \"{}\" is not located", userId);
            throw new NotFoundException(HttpStatus.NOT_FOUND, "a user with such an ID = \"" + userId + "\" is not located");
        } else {
            Set<Integer> likes = filmToUpdate.getLikes();
            likes.remove(userId);
            filmToUpdate.setLikes(likes);
            return filmToUpdate;
        }
    }

    @Override
    public Set<Film> getPopularFilms(int count) {
        Set<Film> result;
        HashMap<Integer, Film> fromRepo = filmStorage.getFilmsFromRepo();
        result = fromRepo.values().stream()
                .sorted(Comparator.comparing((Film f) -> f.getLikes().size()).reversed())
                .limit(count)
                .collect(Collectors.toSet());
        return result;
    }
}
