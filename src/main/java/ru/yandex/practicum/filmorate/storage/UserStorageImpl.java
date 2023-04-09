package ru.yandex.practicum.filmorate.storage;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.HashMap;

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
}
