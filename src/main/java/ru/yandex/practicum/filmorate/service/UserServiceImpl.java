package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorageImpl;

import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@Validated
public class UserServiceImpl implements UserService {
    UserStorageImpl userStorage;

    @Autowired
    public UserServiceImpl(UserStorageImpl userStorage) {
        this.userStorage = userStorage;
    }

    private static int id = 1;

    @Override
    public @Valid User updateUsers(@Valid User user) {
        User userToUpdate = userStorage.getUserFromRepo(user);
        if (userToUpdate == null) {
            log.error("User don't find");
            throw new NotFoundException(HttpStatus.NOT_FOUND, "User not found");
        } else if (userToUpdate.getName() == null || userToUpdate.getName().equals("")) {
            user.setName(user.getLogin());
            userStorage.addUserToRepo(user);
            return user;
        } else {
            userStorage.addUserToRepo(user);
            return user;
        }
    }

    @Override
    public @Valid User addUsers(@Valid User user) {
        if (user.getId() == 0) {
            user.setId(id++);
        }
        if (user.getName() == null || user.getName().equals("")) {
            user.setName(user.getLogin());
        }
        userStorage.addUserToRepo(user);
        return user;
    }

    @Override
    public HashMap<Integer, User> getAllUsers() {
        return userStorage.getUsersFromRepo();
    }

    @Override
    public User getUserById(int userId) {
        return userStorage.getUserFromRepoById(userId);
    }

    @Override
    public Set<User> getFriendsById(int userId) {
        User userFromRepo = userStorage.getUserFromRepoById(userId);
        Set<Integer> friendsIds = userFromRepo.getFriends();
        Set<User> friends = friendsIds.stream()
                .map(userStorage::getUserFromRepoById)
                .sorted(Comparator.comparingInt(User::getId))
                .collect(Collectors.toCollection(LinkedHashSet::new));

        return friends;
    }

    @Override
    public User addFriends(int userId, int friendId) {
        if (userId == friendId) {
            throw new NotFoundException(HttpStatus.NOT_ACCEPTABLE, "Пользователь с id не может добавить сам себя в друзья");
        } else if (userStorage.getUserFromRepoById(friendId) == null) {
            throw new NotFoundException(HttpStatus.NOT_FOUND, "Пользователь с id не найден, невозможно добавить его в друзья");
        } else {
            User userFromRepo = userStorage.getUserFromRepoById(userId);
            Set<Integer> friends = userFromRepo.getFriends();
            friends.add(friendId);

            User userFromRepo2 = userStorage.getUserFromRepoById(friendId);
            Set<Integer> friends2 = userFromRepo2.getFriends();
            friends2.add(userId);

            return userFromRepo;
        }
    }

    @Override
    public User removeFriends(int userId, int friendId) {
        if (userId == friendId) {
            throw new NotFoundException(HttpStatus.NOT_ACCEPTABLE, "Пользователь с id не может быть сам у себя в друзьях");
        } else if (
                userStorage.getUserFromRepoById(friendId) == null) {
            throw new NotFoundException(HttpStatus.NOT_FOUND, "Пользователь с id не найден, невозможно удалить его из друзей");
        } else {
            User userFromRepo = userStorage.getUserFromRepoById(userId);
            Set<Integer> friends = userFromRepo.getFriends();
            friends.remove(friendId);

            return userFromRepo;
        }
    }

    @Override
    public Set<User> getCommonFriendsById(int userId, int otherId) {
        User userFromRepo = userStorage.getUserFromRepoById(userId);
        User otherUserFromRepo = userStorage.getUserFromRepoById(otherId);
        Set<Integer> userFriends = userFromRepo.getFriends();
        Set<Integer> otherUserFriends = otherUserFromRepo.getFriends();

        Set<Integer> userFriendsCopy = new HashSet<>(userFriends);

        userFriendsCopy.retainAll(otherUserFriends);
        Set<User> userSet = new HashSet<>();
        userFriendsCopy
                .stream()
                .forEach(integer -> userSet.add(userStorage.getUserFromRepoById(integer)));
        return userSet;
    }
}
