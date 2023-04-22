package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Set;

@Service
public interface UserService {
    User updateUsers(@Valid User user);

    User addUsers(@Valid User user);

    HashMap<Integer, User> getAllUsers();

    User getUserById(int userId);

    Set<User> getFriendsById(int userId);

    User addFriends(int userId, int friendId);

    User removeFriends(int userId, int friendId);

    Set<User> getCommonFriendsById(int userId, int otherId);
}
