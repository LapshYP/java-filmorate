package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

public interface UserStorage {
    User addUserToRepo(User user);

    User getUserFromRepo(User user);

    HashMap<Integer, User> getUsersFromRepo();

    User getUserFromRepoById(int userId);

    User updateUserToRepo(User user);

    void addToFriends(int id, int friendId);

    Set<Integer> getFriends(int userId);

    void deleteFriends(int id, int friendId);

    List<Integer> findCommonFriends(int userId1, int userId2);
}
