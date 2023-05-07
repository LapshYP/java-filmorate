package ru.yandex.practicum.filmorate.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.yandex.practicum.filmorate.dao.UserStorage;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@Validated
@Primary
@RequiredArgsConstructor
public class UserDbServiceImpl implements UserService {

    private final UserStorage userStorage;

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
            userStorage.updateUserToRepo(user);
            return user;
        }
    }

    @Override
    public @Valid User addUsers(@Valid User user) {

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
        Set<Integer> friendsIds = userStorage.getFriends(userId);
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
            userStorage.addToFriends(userId, friendId);
            User userFromRepo = userStorage.getUserFromRepoById(userId);
            Set<Integer> friends = userFromRepo.getFriends();
            friends.add(friendId);
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
            userStorage.deleteFriends(userId, friendId);
            return userFromRepo;
        }
    }

    @Override
    public Set<User> getCommonFriendsById(int userId, int otherId) {
        List<Integer> commonFriends = userStorage.findCommonFriends(userId, otherId);
        Set<User> userSet = new HashSet<>();
        commonFriends
                .forEach(integer -> userSet.add(userStorage.getUserFromRepoById(integer)));
        return userSet;

    }
}
