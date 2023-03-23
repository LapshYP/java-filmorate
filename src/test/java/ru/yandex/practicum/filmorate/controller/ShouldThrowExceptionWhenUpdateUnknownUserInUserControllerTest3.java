package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
class ShouldThrowExceptionWhenUpdateUnknownUserInUserControllerTest3 {
    @Autowired
    private UserController userController;
    User user;

    @Test
    public void shouldThrowExceptionWhenUpdateUnknownUser() {
        user = User.builder()
                .login("dolore")
                .name("Nick Name")
                .email("mail@mail.ru")
                .birthday(LocalDate.of(1946, 8, 20))
                .build();

        user.setId(100);

        NotFoundException e = assertThrows(
                NotFoundException.class, () -> userController.updateUser(user));

        assertEquals("404 NOT_FOUND \"User don't find\"", e.getMessage());
        assertEquals(0,
                userController.getAllUser().size(),
                "Size Equal Test");
    }

}