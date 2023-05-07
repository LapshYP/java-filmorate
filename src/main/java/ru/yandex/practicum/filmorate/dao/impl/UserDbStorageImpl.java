package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.UserStorage;
import ru.yandex.practicum.filmorate.exception.DubleException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Repository
@Primary
@RequiredArgsConstructor
public class UserDbStorageImpl implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public User addUserToRepo(User user) {
        String checkQuery = "SELECT count(*) FROM USERS WHERE USER_LOGIN = ? AND USER_EMAIL = ?";
        int count = jdbcTemplate.queryForObject(checkQuery, new Object[] { user.getLogin(), user.getEmail()}, Integer.class);
        if (count > 0) {
            throw new DubleException("User '" +
                    user.getLogin() +
                    "' already exists");
        }

        String sqlQuery = "insert into Users (USER_LOGIN, USER_EMAIL, USER_NAME, USER_BIRTHDAY) " +
                "SELECT ?, ?, ?, ? WHERE NOT EXISTS" +
                "(SELECT 1 FROM USERS WHERE USER_LOGIN = ? AND USER_EMAIL = ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"user_id"});
            stmt.setString(1, user.getLogin());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getName());
            stmt.setDate(4, Date.valueOf(user.getBirthday()));
            stmt.setString(5, user.getLogin());
            stmt.setString(6, user.getEmail());
            return stmt;
        }, keyHolder);

        user.setId((int) keyHolder.getKey().longValue());
        return user;
    }

    @Override
    public User updateUserToRepo(User user) {
        String sqlQuery = "update Users set USER_EMAIL = ?, USER_LOGIN = ?, USER_NAME = ?, USER_BIRTHDAY = ? where USER_ID = ?";
        jdbcTemplate.update(sqlQuery,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getId());
        return user;
    }

    @Override
    public User getUserFromRepo(User user) {

        String sqlQuery = "SELECT USER_ID, USER_LOGIN, USER_EMAIL, USER_NAME, USER_BIRTHDAY FROM USERS WHERE USER_ID = " + user.getId();
        User result = null;
        try {
            result = jdbcTemplate.queryForObject(
                    sqlQuery,
                    (resultSet, rowNum) -> {
                        User newUser = new User();
                        newUser.setId(Integer.parseInt(resultSet.getString("USER_ID")));
                        newUser.setEmail(resultSet.getString("USER_EMAIL"));
                        newUser.setLogin(resultSet.getString("USER_LOGIN"));
                        newUser.setName(resultSet.getString("USER_NAME"));
                        newUser.setBirthday(LocalDate.parse(resultSet.getString("USER_BIRTHDAY")));
                        return newUser;
                    });
        } catch (EmptyResultDataAccessException e) {
            // обработка ситуации, когда пользователя с указанным ID не было найдено
            throw new NotFoundException(HttpStatus.NOT_FOUND, "User not found");
        }
        return result;
    }

    @Override
    public HashMap<Integer, User> getUsersFromRepo() {
        List<User> allUsers = this.jdbcTemplate.query(
                "select USER_ID, USER_EMAIL, USER_LOGIN, USER_NAME, USER_BIRTHDAY from Users",
                (resultSet, rowNum) -> {
                    User user = new User();
                    user.setId(Integer.parseInt(resultSet.getString("USER_ID")));
                    user.setEmail(resultSet.getString("USER_EMAIL"));
                    user.setLogin(resultSet.getString("USER_LOGIN"));
                    user.setName(resultSet.getString("USER_NAME"));
                    user.setBirthday(LocalDate.parse(resultSet.getString("USER_BIRTHDAY")));
                    return user;
                });
        Map<Integer, User> allUsersMap = allUsers.stream().collect(Collectors.toMap(User::getId, user -> user));

        return (HashMap<Integer, User>) allUsersMap;
    }

    public User getUserFromRepoById(int id) {
        String sqlQuery = "SELECT USER_ID, USER_LOGIN, USER_EMAIL, USER_NAME, USER_BIRTHDAY FROM USERS WHERE USER_ID = ?";
        User result = null;
        try {
            result = jdbcTemplate.queryForObject(
                    sqlQuery,
                    (resultSet, rowNum) -> {
                        User newUser = new User();
                        newUser.setId(Integer.parseInt(resultSet.getString("USER_ID")));
                        newUser.setEmail(resultSet.getString("USER_EMAIL"));
                        newUser.setLogin(resultSet.getString("USER_LOGIN"));
                        newUser.setName(resultSet.getString("USER_NAME"));
                        newUser.setBirthday(LocalDate.parse(resultSet.getString("USER_BIRTHDAY")));
                        return newUser;
                    },
                    id);

        } catch (EmptyResultDataAccessException e) {
            // обработка ситуации, когда пользователя с указанным ID не было найдено
            throw new NotFoundException(HttpStatus.NOT_FOUND, "User with id='" +
                    id +
                    "' not found");
        }
        return result;
    }

@Override
public void addToFriends(int id, int friendId) {
    getUserFromRepoById(id);

    String sqlQuery = "INSERT INTO friendships (user_id, friend_id, friendship_status) " +
            "SELECT ?, ?, ? " +
            "WHERE NOT EXISTS (" +
            "SELECT 1 FROM friendships WHERE user_id = ? AND friend_id = ? AND friendship_status = ?" +
            ")";
    KeyHolder keyHolder = new GeneratedKeyHolder();
    jdbcTemplate.update(connection -> {
        PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"USER_ID"});
        stmt.setInt(1, id);
        stmt.setInt(2, friendId);
        stmt.setBoolean(3, true);
        stmt.setInt(4, id);
        stmt.setInt(5, friendId);
        stmt.setBoolean(6, true);

        return stmt;
    }, keyHolder);
}

    @Override
    public Set<Integer> getFriends(int userId) {
        String query = "SELECT FRIEND_ID FROM FRIENDSHIPS WHERE USER_ID=?";
        Set<Integer> friends = new HashSet<>();
        this.jdbcTemplate.query(query, new Object[]{userId}, (rs, rowNum) -> {
            friends.add(rs.getInt("FRIEND_ID"));
            return null;
        });
        return friends;
    }

    @Override
    public void deleteFriends(int userId, int friendId) {
        String sql = "delete from FRIENDSHIPS where USER_ID = ? and FRIEND_ID = ?";
        int rowsDeleted = jdbcTemplate.update(sql, userId, friendId);
        if (rowsDeleted == 0) {
            throw new NotFoundException(HttpStatus.NOT_FOUND,"Friendship not found with USER_ID=" + userId + " and FRIEND_ID=" + friendId);
        }
    }

    @Override
    public List<Integer> findCommonFriends(int userId1, int userId2) {
        String sql = "SELECT f1.FRIEND_ID FROM FRIENDSHIPS f1\n" +
                "INNER JOIN FRIENDSHIPS f2 ON f1.FRIEND_ID = f2.FRIEND_ID\n" +
                "WHERE f1.USER_ID = ? AND f2.USER_ID = ? AND f1.FRIENDSHIP_STATUS = TRUE AND f2.FRIENDSHIP_STATUS = TRUE;";
        List<Integer> integerList = jdbcTemplate.queryForList(sql, Integer.class, userId1, userId2);
        integerList.stream().distinct().collect(Collectors.toList());
        return integerList;
    }

}
