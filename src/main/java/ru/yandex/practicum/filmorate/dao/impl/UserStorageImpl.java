package ru.yandex.practicum.filmorate.dao.impl;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.UserStorage;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

@Repository
public class UserStorageImpl implements UserStorage {
    private HashMap<Integer, User> userHashMap = new HashMap<>();

    @Override
    public User addUserToRepo(User user) {
        userHashMap.put(user.getId(), user);
        return user;
    }

    @Override
    public User getUserFromRepo(User user) {
        User userToUpdate = userHashMap.get(user.getId());
        return userToUpdate;
    }

    @Override
    public HashMap<Integer, User> getUsersFromRepo() {
        return userHashMap;
    }

    public User getUserFromRepoById(int userId) {
        User user = userHashMap.get(userId);
        if (user == null) {
            throw new NotFoundException(HttpStatus.NOT_FOUND, "User with this ID dont't found");
        }
        return user;
    }

    @Override
    public User updateUserToRepo(User user) {
        return null;
    }

    @Override
    public void addToFriends(int id, int friendId) {

    }

    @Override
    public Set<Integer> getFriends(int userId) {
        return null;
    }

    @Override
    public void deleteFriends(int id, int friendId) {

    }

    @Override
    public List<Integer> findCommonFriends(int userId1, int userId2) {
        return null;
    }
}
