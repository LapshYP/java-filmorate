package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Slf4j
@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {

    private final FilmService filmService;

    @GetMapping("/{filmId}")
    public Film findUserById(@PathVariable int filmId) {
        Film filmById = filmService.getFilmById(filmId);
        log.debug("Film with id = \"{}\" ", filmId);
        return filmById;
    }

    @PutMapping("/{filmId}/like/{userId}")
    public Film addLike(@PathVariable int filmId, @PathVariable int userId) {
        log.debug("Film with id = \"{}\"  added like to film with id = \"{}\" ", filmId, userId);
        return filmService.addLike(filmId, userId);
    }

    @DeleteMapping("/{filmId}/like/{userId}")
    public Film removeLike(@PathVariable int filmId, @PathVariable int userId) {
        log.debug("Film with id = \"{}\" removed like from film with id = \"{}\" ", filmId, userId);
        return filmService.removeLike(filmId, userId);
    }

    @GetMapping("/popular")
    public Set<Film> popularFilmsCount(@RequestParam(defaultValue = "10") int count) {
        Set<Film> popularFilms = filmService.getPopularFilms(count);
        log.debug("list films by likes");

        return popularFilms;
    }

    @GetMapping
    public List<Film> getAllFIlms() {
        List<Film> collect = new ArrayList<>(filmService.getAllFilms().values());
        log.debug("There is {} films in filmorate", filmService.getAllFilms().size());

        return collect;
    }

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) {
        Film filmToAdd = filmService.addFilms(film);
        log.debug("Film with name = \"{}\"  added", filmToAdd.getName());
        return filmToAdd;
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        Film filmToUpdate = filmService.updateFilm(film);
        log.debug("Film with name = \"{}\"  updated", filmToUpdate.getName());
        return filmToUpdate;
    }
}
