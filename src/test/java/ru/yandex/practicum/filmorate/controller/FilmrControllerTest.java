package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.ConstraintViolationException;
import java.time.LocalDate;
import java.util.List;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
class FilmrControllerTest {

    @Autowired
    private FilmController filmController;
    private Film film;

    @BeforeEach
    public void createController() {
        film = Film.builder()
                .name("nisi eiusmod")
                .description("adipisicing")
                .releaseDate(LocalDate.of(1967, 3, 25))
                .duration(100)
                .build();
    }

    @Test
    public void shouldCreateUser() {

        filmController.addFilm(film);
        List<Film> allFIlmss = filmController.getAllFIlms();

        assertEquals(3, allFIlmss.size(), "Size Equal Test");

        Film filmToCheck = allFIlmss.get(0);

        assertEquals(film.getName(), filmToCheck.getName(), "Name Equal Test");
        assertEquals(film.getDescription(), filmToCheck.getDescription(), "Description Equal Test");

        assertEquals(film.getReleaseDate(), filmToCheck.getReleaseDate(), "ReleaseDate Equal Test");
        assertEquals(film.getDuration(), filmToCheck.getDuration(), "Duration Equal Test");
    }

    @Test
    public void shouldThrowExceptionWhenUpdateUnknownUser() {
        film.setId(10);

        NotFoundException e = assertThrows(
                NotFoundException.class, () -> filmController.updateFilm(film));

        assertEquals("404 NOT_FOUND \"Film with name = \"nisi eiusmod\" don't find\"", e.getMessage());
        assertEquals(2,
                filmController.getAllFIlms().size(),
                "Size Equal Test");
    }

    @Test
    public void shouldGetAllUserTest() {
        filmController.addFilm(film);
        Film film100 = film;
        film100.setId(100);

        filmController.addFilm(film100);

        assertEquals(2, filmController.getAllFIlms().size(),
                "Size Equal Test");
    }
}