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
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.ConstraintViolationException;
import java.time.LocalDate;
import java.util.*;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
class FilmrControllerTest {

    @Autowired
    private FilmController filmController;
    @Autowired
    private UserController userController;
    private Film film;
    private User user;

    @BeforeEach
    public void createController() {
        film = Film.builder()
                .name("nisi eiusmod")
                .description("adipisicing")
                .releaseDate(LocalDate.of(1967, 3, 25))
                .duration(100)
                .build();

        user = User.builder()
                .login("dolore")
                .name("Nick Name")
                .email("mail@mail.ru")
                .birthday(LocalDate.of(1946, 8, 20))
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

    @Test
    public void shouldPutDeliteGetPopularFilmLike() {
        filmController.addFilm(film);
        Film film1 = film;
        userController.addUser(user);
        User user1 = user;
        userController.addUser(user1);
        User user2 = user;
        userController.addUser(user2);

        film.setLikes(new HashSet<>());

        filmController.addLike(film.getId(), user.getId());

        assertEquals(1, filmController.getAllFIlms().get(0).getLikes().size(),
                "Size Equal Test");


        filmController.removeLike(film.getId(), user.getId());

        assertEquals(0, filmController.getAllFIlms().get(0).getLikes().size(),
                "Size Equal Test");

        //добавляем film - 1 like, film1 - 2 likes
        filmController.addLike(film.getId(), user.getId());
        filmController.addLike(film1.getId(), user.getId());
        filmController.addLike(film1.getId(), user1.getId());
        Optional<Film> result1 = filmController.popularFilmsCount(10).stream().findFirst();
        Optional<Film> result2 = Optional.of(film1);
        //выводит фильм 2 т.к. у него больше лайков и он есть в списке популярных фильмов
        assertEquals(result2, result1,
                "Equal Test");
    }


}