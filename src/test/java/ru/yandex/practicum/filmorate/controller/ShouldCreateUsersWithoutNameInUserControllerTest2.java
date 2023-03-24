package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class ShouldCreateUsersWithoutNameInUserControllerTest2 {
    @Autowired
    private UserController userController;
    User user;

    @Test
    public void shouldCreateUserWithoutName() {
        user = User.builder()
                .login("dolore")
                .name("Nick Name")
                .email("mail@mail.ru")
                .birthday(LocalDate.of(1946, 8, 20))
                .build();
        user.setName("");
        userController.addUser(user);

        List<User> allUsers = userController.getAllUser();

        assertEquals(1, allUsers.size(),
                "Size Equal Test");

        User userToCheck = allUsers.get(0);

        assertEquals(user.getLogin(), userToCheck.getName(), "Login Equal Name Test");
    }


}