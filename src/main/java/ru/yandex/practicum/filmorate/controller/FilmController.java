package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmServiceImpl;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    private final FilmServiceImpl filmServiceImpl;

    @Autowired
    public FilmController(FilmServiceImpl filmServiceImpl) {
        this.filmServiceImpl = filmServiceImpl;
    }

    @GetMapping("/{filmId}")
    public Film findUserById(@PathVariable int filmId) {
        Film filmById = filmServiceImpl.getFilmById(filmId);
        log.debug("Film with id = \"{}\" ", filmId);
        return filmById;
    }

    @PutMapping("/{filmId}/like/{userId}")
    public Film addLike(@PathVariable int filmId, @PathVariable int userId) {
        log.debug("Film with id = \"{}\"  added like to film with id = \"{}\" ", filmId, userId);
        return filmServiceImpl.addLike(filmId, userId);
    }

    @DeleteMapping("/{filmId}/like/{userId}")
    public Film removeLike(@PathVariable int filmId, @PathVariable int userId) {
        log.debug("Film with id = \"{}\" removed like from film with id = \"{}\" ", filmId, userId);
        return filmServiceImpl.removeLike(filmId, userId);
    }

    @GetMapping("/popular")
    public Set<Film> popularFilmsCount(@RequestParam(defaultValue = "10") int count) {
        Set<Film> popularFilms = filmServiceImpl.getPopularFilms(count);
        log.debug("list films by likes");

        return popularFilms;
    }

    @GetMapping
    public List<Film> getAllFIlms() {
        List<Film> collect = new ArrayList<>(filmServiceImpl.getAllFilms().values());
        log.debug("There is {} films in filmorate", filmServiceImpl.getAllFilms().size());

        return collect;
    }

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) {
        Film filmToAdd = filmServiceImpl.addFilms(film);
        log.debug("Film with name = \"{}\"  added", filmToAdd.getName());
        return filmToAdd;
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        Film filmToUpdate = filmServiceImpl.updateFilm(film);
        log.debug("Film with name = \"{}\"  updated", filmToUpdate.getName());
        return filmToUpdate;
    }
}
