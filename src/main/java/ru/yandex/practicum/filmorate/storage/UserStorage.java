package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.HashMap;

public interface UserStorage {
    User addUserToRepo (User user);
    User getUserFromRepo (User user);
    HashMap<Integer, User> getUsersFromRepo();

}
