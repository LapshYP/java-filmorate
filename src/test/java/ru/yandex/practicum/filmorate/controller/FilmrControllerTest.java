package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

//@RunWith(SpringRunner.class)
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
    public void shouldPutDeliteGetPopularFilmLike() {
        filmController.addFilm(film);
        Film film1 = film;
        userController.addUser(user);
        User user1 = user;
        user1.setId(100);
        userController.addUser(user1);

        Film filmFromRepo = filmController.addLike(this.film.getId(), user.getId());

        assertEquals(1, filmFromRepo.getLikes().size(),
                "Size Equal Test");

        Film removeLike = filmController.removeLike(this.film.getId(), user.getId());

        assertEquals(0, removeLike.getLikes().size(),
                "Size Equal Test");

        //добавляем film - 1 like, film1 - 2 likes
        filmController.addLike(film1.getId(), user.getId());
        filmController.addLike(film1.getId(), user1.getId());
        Optional<Film> result1 = filmController.popularFilmsCount(10).stream().findFirst();
        Optional<Film> result2 = Optional.of(film1);
        //выводит фильм 2 т.к. у него больше лайков и он есть в списке популярных фильмов
        assertEquals(result2, result1,
                "Equal Test");
    }
}