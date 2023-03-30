package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmServiceImpl;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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


    @GetMapping
    public List<Film> getAllFIlms() {

        log.debug("There is {} films in filmorate", filmServiceImpl.getAllFilms().size());

        return filmServiceImpl.getAllFilms()
                .values()
                .stream()
                .collect(Collectors.toList());
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
