package ru.yandex.practicum.filmorate.controller.filmTests;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class FilmrControllerTest1 {
    @Autowired
    private FilmController filmController;
    Film film;

    @Test
    public void shouldCreateUser() {
        film = Film.builder()
                .name("nisi eiusmod")
                .description("adipisicing")
                .releaseDate(LocalDate.of(1967, 3, 25))
                .duration(100)
                .build();
        filmController.addFilm(film);
        List<Film> allFIlmss = filmController.getAllFIlms();

        assertEquals(1, allFIlmss.size(), "Size Equal Test");

        Film filmToCheck = allFIlmss.get(0);

        assertEquals(film.getName(), filmToCheck.getName(), "Name Equal Test");
        assertEquals(film.getDescription(), filmToCheck.getDescription(), "Description Equal Test");

        assertEquals(film.getReleaseDate(), filmToCheck.getReleaseDate(), "ReleaseDate Equal Test");
        assertEquals(film.getDuration(), filmToCheck.getDuration(), "Duration Equal Test");
    }
}