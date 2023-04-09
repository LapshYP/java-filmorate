package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserServiceImpl;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    public final UserServiceImpl userServiceImpl;

    @Autowired
    public UserController(UserServiceImpl userServiceImpl) {
        this.userServiceImpl = userServiceImpl;
    }

    @GetMapping
    public List<User> getAllUser() {
        var users = userServiceImpl.getAllUsers();
        List<User> userList = new ArrayList<>(users.values());
        log.debug("There are {} users in filmorate", userServiceImpl.getAllUsers().size());
        return userList;
    }

    @GetMapping("/{userId}")
    public User findUserById(@PathVariable int userId) {
        User userById = userServiceImpl.getUserById(userId);
        log.debug("User with id = \"{}\"  ", userId);
        return userById;
    }

    @PutMapping("/{userId}/friends/{friendId}")
    public User addFriends(@PathVariable int userId, @PathVariable int friendId) {
        User addFriends = userServiceImpl.addFriends(userId, friendId);
        log.debug("to user with id = \"{}\"  added frinend with id = \"{}\" ", userId, friendId);
        return addFriends;
    }

    @DeleteMapping("/{userId}/friends/{friendId}")
    public User removeFriends(@PathVariable int userId, @PathVariable int friendId) {
        User removeFriends = userServiceImpl.removeFriends(userId, friendId);
        log.debug("to user with id = \"{}\" removed friend with id = \"{}\" ", userId, friendId);
        return removeFriends;
    }

    @GetMapping("/{userId}/friends/common/{otherId}")
    public Set<User> findUserFriendsById(@PathVariable int userId, @PathVariable int otherId) {
        Set<User> commonFriendsById = userServiceImpl.getCommonFriendsById(userId, otherId);
        log.debug("user with id = \"{}\" have a common friend with other user with id = \"{}\" ", userId, otherId);
        return commonFriendsById;
    }

    @GetMapping("/{userId}/friends")
    public Set<User> findUserFriendsById(@PathVariable int userId) {
        Set<User> friendsById = userServiceImpl.getFriendsById(userId);
        log.debug(" user with id = \"{}\" got friends", userId);
        return friendsById;
    }

    @PostMapping
    public @Valid User addUser(@Valid @RequestBody User user) {
        User userToAdd = userServiceImpl.addUsers(user);
        log.debug("user with name = \"{}\"  added", user.getName());
        return userToAdd;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        User updetedUser = userServiceImpl.updateUsers(user);
        log.debug("user with name = \"{}\" updated", user.getName());
        return updetedUser;
    }
}
