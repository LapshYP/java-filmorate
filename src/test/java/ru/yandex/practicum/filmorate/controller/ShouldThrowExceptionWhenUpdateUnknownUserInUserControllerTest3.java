package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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