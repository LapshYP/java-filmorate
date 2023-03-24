package ru.yandex.practicum.filmorate.controller.filmTests;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class FilmrControllerTest4 {
    @Autowired
    private FilmController filmController;
    Film film;

    @Test
    public void shouldGetAllFilmTest() {
        film = Film.builder()
                .name("nisi eiusmod")
                .description("adipisicing")
                .releaseDate(LocalDate.of(1967, 3, 25))
                .duration(100)
                .build();
        filmController.addFilm(film);
        Film film100 = film;
        film100.setId(100);

        filmController.addFilm(film100);

        assertEquals(3, filmController.getAllFIlms().size(),
                "Size Equal Test");
    }
}