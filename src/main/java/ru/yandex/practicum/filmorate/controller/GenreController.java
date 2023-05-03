package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/genres")
@RequiredArgsConstructor
public class GenreController {

    public final GenreService genreService;

    @GetMapping
    public List<Genre> getAllMpa() {
        var genres = genreService.getAllGenres();
        List<Genre> genresList = new ArrayList<>(genres.values());
        log.debug("There are {} users in filmorate", genreService.getAllGenres().size());
        return genresList;
    }

    @GetMapping("/{genreId}")
    public Genre findMpaById(@PathVariable int genreId) {
        Genre genreById = genreService.getGenreById(genreId);
        log.debug("Genre with id = \"{}\"  ", genreId);
        return genreById;
    }
}
