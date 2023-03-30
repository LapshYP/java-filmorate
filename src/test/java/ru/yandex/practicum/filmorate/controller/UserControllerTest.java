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
class UserControllerTest {

    @Autowired
    private UserController userController;
    private User user;

    @BeforeEach
    public void createController() {
        user = User.builder()
                .login("dolore")
                .name("Nick Name")
                .email("mail@mail.ru")
                .birthday(LocalDate.of(1946, 8, 20))
                .build();
    }

    @Test
    public void shouldCreateUser() {

        userController.addUser(user);
        List<User> allUsers = userController.getAllUser();

        assertEquals(3, allUsers.size(), "Size Equal Test");

        User userToCheck = allUsers.get(0);

        assertEquals(user.getName(), userToCheck.getName(), "Name Equal Test");
        assertEquals(user.getLogin(), userToCheck.getLogin(), "Login Equal Test");
        assertEquals(user.getEmail(), userToCheck.getEmail(), "Email Equal Test");
        assertEquals(user.getBirthday(), userToCheck.getBirthday(), "Birthday Equal Test");
    }

    @Test
    public void shouldCreateUserWithoutName() {
        user.setName("");
        userController.addUser(user);

        List<User> allUsers = userController.getAllUser();

        assertEquals(4, allUsers.size(),
                "Size Equal Test");

        User userToCheck = allUsers.get(2);

        assertEquals(user.getLogin(), userToCheck.getName(), "Login Equal Name Test");
    }


    @Test
    public void shouldThrowExceptionWhenUpdateUnknownUser() {
        user.setId(10);

        NotFoundException e = assertThrows(
                NotFoundException.class, () -> userController.updateUser(user));

        assertEquals("404 NOT_FOUND \"User don't find\"", e.getMessage());
        assertEquals(2,
                userController.getAllUser().size(),
                "Size Equal Test");
    }

    @Test
    public void shouldGetAllUserTest() {
        userController.addUser(user);
        User user100 = user;
        user100.setId(100);

        userController.addUser(user100);

        assertEquals(2, userController.getAllUser().size(),
                "Size Equal Test");
    }
}