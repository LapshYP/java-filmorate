package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.ConstraintViolationException;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class ShouldCreateFilmWithoutNameInFilmrControllerTest2 {
    @Autowired
    private FilmController filmController;
    Film film;

    @Test
    public void shouldCreateFilmWithoutName() {
        film = Film.builder()
                .name("")
                .description("adipisicing")
                .releaseDate(LocalDate.of(1967, 3, 25))
                .duration(100)
                .build();
        film.setName("");

        ConstraintViolationException e = assertThrows(
                ConstraintViolationException.class, () -> filmController.addFilm(film));

        assertEquals("addFilms.film.name: не должно быть пустым", e.getMessage());
        assertEquals(0,
                filmController.getAllFIlms().size(),
                "Size Equal Test");
    }

}