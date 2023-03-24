package ru.yandex.practicum.filmorate.controller.userTests;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class UserControllerTest4 {
    @Autowired
    private UserController userController;
    User user;

    @Test
    public void shouldGetAllUserTest() {
        user = User.builder()
                .login("dolore")
                .name("Nick Name")
                .email("mail@mail.ru")
                .birthday(LocalDate.of(1946, 8, 20))
                .build();
        userController.addUser(user);
        User user100 = user;
        user100.setId(100);

        userController.addUser(user100);

        assertEquals(2, userController.getAllUser().size(),
                "Size Equal Test");
    }
}