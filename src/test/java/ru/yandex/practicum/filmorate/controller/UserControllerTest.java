package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class UserControllerTest {

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

        userController.addUser(user);
        List<User> allUsers = userController.getAllUser();

        assertEquals(1, allUsers.size(), "Size Equal Test");

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

        assertEquals(1, allUsers.size(),
                "Size Equal Test");

        User userToCheck = allUsers.get(0);

        assertEquals(user.getLogin(), userToCheck.getName(), "Login Equal Name Test");
    }

    @Test
    public void shouldThrowExceptionWhenUpdateUnknownUser() {
        user.setId(10);

        NotFoundException e = assertThrows(
                NotFoundException.class, () -> userController.updateUser(user));

        assertEquals("404 NOT_FOUND \"User not found\"", e.getMessage());
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

    @Test
    public void shouldAddFriendTest() {
        userController.addUser(user);
        User user1 = new User();
        user1.setLogin("login");
        userController.addUser(user1);
        user.setFriends(new HashSet<>());
        user1.setFriends(new HashSet<>());

        User addFriends = userController.addFriends(user.getId(), user1.getId());

        //при добавлении в друзьях должен быть юзер с id от user1
        Set<Integer> integerSet = new HashSet<>();
        integerSet.add(user1.getId());
        assertEquals(integerSet, addFriends.getFriends(),
                "Equal Test");

        Set<User> userSet = new HashSet<>();
        userSet.add(user);
        assertEquals(userSet, userController.findUserFriendsById(user1.getId()),
                "Equal Test");

        User deliteFriend = userController.removeFriends(user.getId(), user1.getId());
        integerSet.remove(user1.getId());
        assertEquals(integerSet, addFriends.getFriends(),
                "Equal Test");
    }
}