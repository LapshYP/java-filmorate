package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.List;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
class ShouldThrowExceptionWhenUpdateUnknownFilmInFilmrControllerTest3 {
    @Autowired
    private FilmController filmController;
    Film film;

    @Test
    public void shouldThrowExceptionWhenUpdateUnknownFilm() {
        film = Film.builder()
                .name("nisi eiusmod")
                .description("adipisicing")
                .releaseDate(LocalDate.of(1967, 3, 25))
                .duration(100)
                .build();

        film.setId(1000);

        NotFoundException e = assertThrows(
                NotFoundException.class, () -> filmController.updateFilm(film));

        assertEquals("404 NOT_FOUND \"Film with name = \"nisi eiusmod\" don't find\"", e.getMessage());
        assertEquals(0,
                filmController.getAllFIlms().size(),
                "Size Equal Test");
    }

}