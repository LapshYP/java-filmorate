package ru.yandex.practicum.filmorate.dao.impl;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.FilmStorage;
import ru.yandex.practicum.filmorate.exception.DubleException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Repository
@AllArgsConstructor
@Primary
public class FilmDbStorageImpl implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Film addFilmLikeToRepo(Film filmToLike, int userId) {

        String checkQuery = "SELECT COUNT(*) FROM LIKES WHERE FILM_ID = ? AND USER_ID = ?";
        int count = jdbcTemplate.queryForObject(checkQuery, new Object[]{filmToLike.getId(), userId}, Integer.class);

        if (count > 0) {
            throw new DubleException("Like to film '" +
                    filmToLike.getName() +
                    "' from user id='" +
                    userId +
                    "' already exists");
        }
        KeyHolder keyHolder = new GeneratedKeyHolder();
        String sqlQuery = "INSERT INTO LIKES (FILM_ID, USER_ID)" +
                "SELECT ?, ?" +
                "WHERE NOT EXISTS (" +
                "    SELECT 1" +
                "    FROM LIKES" +
                "    WHERE FILM_ID = ? AND USER_ID = ?" +
                ")";

        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery);
            stmt.setInt(1, filmToLike.getId());
            stmt.setInt(2, userId);
            stmt.setInt(3, filmToLike.getId());
            stmt.setInt(4, userId);
            return stmt;
        }, keyHolder);


        return filmToLike;
    }

    @Override
    public Film addFilmToRepo(Film film) {
        String checkQuery = "SELECT count(*) FROM FILMS WHERE FILM_NAME = ? AND FILM_RELEASE_DATE = ? AND FILM_DURATION = ?";
        int count = jdbcTemplate.queryForObject(checkQuery, new Object[]{film.getName(), film.getReleaseDate(), film.getDuration()}, Integer.class);
        if (count > 0) {
            throw new DubleException("Film '" + film.getName() + "' already exists");
        }

        List<Genre> genres = film.getGenres().stream().distinct().collect(Collectors.toList());
        List<Genre> incorrectGenres = new ArrayList<>();
        for (Genre genre : genres) {
            String checkGenreQuery = "SELECT count(*) FROM GENRES WHERE GENRES.GENRE_ID = ?";
            int genreCount = jdbcTemplate.queryForObject(checkGenreQuery, new Object[]{genre.getId()}, Integer.class);
            if (genreCount == 0) {
                incorrectGenres.add(genre);
            }
        }
        if (!incorrectGenres.isEmpty()) {
            throw new NotFoundException(HttpStatus.NOT_FOUND, "The following genres are incorrect: " + incorrectGenres);
        }
        if (film.getMpa().getId() == null) {
            throw new NotFoundException(HttpStatus.NOT_FOUND, "MPA rating id is required");

        }
        KeyHolder keyHolder = new GeneratedKeyHolder();

        String sqlQuery = "INSERT INTO FILMS (FILM_NAME, FILM_DESCRIPTION, FILM_RELEASE_DATE, "
                + "FILM_DURATION, FILM_RATE, FILM_MPA)\n"
                + "SELECT ?, ?, ?, ?, ?, ?\n"
                + "WHERE NOT EXISTS (\n"
                + "  SELECT 1\n"
                + "  FROM FILMS\n"
                + "  WHERE FILM_NAME = ? AND FILM_RELEASE_DATE = ? AND FILM_DURATION = ?)";
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"film_id"});
            stmt.setString(1, film.getName());
            stmt.setString(2, film.getDescription());
            stmt.setDate(3, Date.valueOf(film.getReleaseDate()));
            stmt.setInt(4, film.getDuration());
            stmt.setInt(5, film.getRate());
            stmt.setInt(6, film.getMpa().getId());
            stmt.setString(7, film.getName());
            stmt.setDate(8, Date.valueOf(film.getReleaseDate()));
            stmt.setInt(9, film.getDuration());
            return stmt;
        }, keyHolder);


        film.setId((int) keyHolder.getKey().longValue());

        String queryDelete = "delete from FILM_GENRES where FILM_ID = ?";
        jdbcTemplate.update(queryDelete, film.getId());

        List<Genre> genress = film.getGenres().stream().distinct().collect(
                Collectors.toList());

        String sqlQueryForFilmsToGenres = "insert into FILM_GENRES (FILM_ID, genre_id)" +
                "values (?, ?)";
        jdbcTemplate.batchUpdate(sqlQueryForFilmsToGenres, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                Genre genre = genress.get(i);
                ps.setLong(1, film.getId());
                ps.setLong(2, genre.getId());
            }

            @Override
            public int getBatchSize() {
                return genres.size();
            }
        });

        Film filmToSetGenres = film;
        filmToSetGenres.setGenres(genres);
        return filmToSetGenres;

    }

    @Override
    public Film updateFilm(Film film) {
        String sqlQuery = "update FILMS set FILM_NAME = ?, FILM_RELEASE_DATE = ?, " +
                "FILM_DURATION = ?, FILM_DESCRIPTION = ?, FILM_RATE = ?, FILM_MPA = ? where FILM_ID = ?";
        jdbcTemplate.update(sqlQuery,
                film.getName(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getDescription(),
                film.getRate(),
                film.getMpa().getId(),
                film.getId());
        if (film.getGenres() != null) {
            addGenres(film);
        }
        return film;
    }

    private Film addGenres(Film film) {
        String queryDelete = "delete from FILM_GENRES where FILM_ID = ?";
        jdbcTemplate.update(queryDelete, film.getId());

        List<Genre> genres = film.getGenres().stream().distinct().collect(
                Collectors.toList());

        String sqlQueryForFilmsToGenres = "insert into FILM_GENRES (FILM_ID, genre_id)" +
                "values (?, ?)";
        jdbcTemplate.batchUpdate(sqlQueryForFilmsToGenres, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                Genre genre = genres.get(i);
                ps.setLong(1, film.getId());
                ps.setLong(2, genre.getId());
            }

            @Override
            public int getBatchSize() {
                return genres.size();
            }
        });

        Film filmToSetGenres = film;
        filmToSetGenres.setGenres(genres);
        return filmToSetGenres;
    }

    @Override
    public Film getFilmFromRepo(Film film) {

        String sqlQuery = "select FILM_ID, FILM_NAME, FILM_DESCRIPTION, FILM_RELEASE_DATE, FILM_DURATION from FILMS  WHERE FILM_ID = " + film.getId();
        Film result = null;
        try {
            result = jdbcTemplate.queryForObject(
                    sqlQuery,
                    (resultSet, rowNum) -> {
                        Film newFilm = new Film();
                        newFilm.setId(Integer.parseInt(resultSet.getString("FILM_ID")));
                        newFilm.setName(resultSet.getString("FILM_NAME"));
                        newFilm.setDescription(resultSet.getString("FILM_DESCRIPTION"));
                        newFilm.setDuration(Integer.parseInt(resultSet.getString("FILM_DURATION")));
                        newFilm.setReleaseDate(LocalDate.parse(resultSet.getString("FILM_RELEASE_DATE")));
                        return newFilm;
                    });
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException(HttpStatus.NOT_FOUND, "Film not found");
        }
        return result;
    }

    @Override
    public HashMap<Integer, Film> getFilmsFromRepo() {
        String sqlQuery = "select FILM_ID, FILM_NAME, FILM_DESCRIPTION, FILM_RELEASE_DATE," +
                " FILM_DURATION,FILM_RATE, FILM_MPA, MPA_NAME from FILMS " +
                "INNER JOIN MPA ON MPA.MPA_ID=FILMS.FILM_MPA";
        List<Film> allFilms = this.jdbcTemplate.query(
                sqlQuery,
                (resultSet, rowNum) -> {
                    Film film = new Film();
                    film.setId(Integer.parseInt(resultSet.getString("FILM_ID")));
                    film.setName(resultSet.getString("FILM_NAME"));
                    film.setDescription(resultSet.getString("FILM_DESCRIPTION"));
                    film.setDuration(Integer.parseInt(resultSet.getString("FILM_DURATION")));
                    film.setReleaseDate(LocalDate.parse(resultSet.getString("FILM_RELEASE_DATE")));
                    film.setRate(Integer.parseInt(resultSet.getString("FILM_RATE")));
                    film.setMpa(new MPA(Integer.parseInt(resultSet.getString("FILM_MPA"))));
                    film.setMpa(new MPA(Integer.parseInt(resultSet.getString("FILM_MPA")), resultSet.getString("MPA_NAME")));
                    return film;
                });
        for (Film film : allFilms) {
            if (film != null) {
                takeGenreFromDb(film);
            }
        }
        Map<Integer, Film> filmMap = allFilms.stream().collect(Collectors.toMap(Film::getId, film -> film));
        return (HashMap<Integer, Film>) filmMap;
    }

    @Override
    public HashMap<Integer, Film> getFilmsFromRepoWithCount(int count) {
        String sqlQuery = "SELECT FILMS.FILM_ID, FILM_NAME, FILM_DESCRIPTION, FILM_RELEASE_DATE, " +
                "FILM_DURATION, FILM_RATE, FILM_MPA, MPA_NAME, COUNT(user_id) as likes " +
                "FROM FILMS " +
                "INNER JOIN MPA ON FILM_MPA = MPA.MPA_ID " +
                "LEFT JOIN LIKES ON FILMS.FILM_ID = LIKES.film_id " +
                "GROUP BY FILMS.FILM_ID, FILM_NAME, FILM_DESCRIPTION, FILM_RELEASE_DATE, " +
                "FILM_DURATION, FILM_RATE, FILM_MPA, MPA_NAME " +
                "ORDER BY likes DESC LIMIT " +
                count +
                "";
        List<Film> allFilms = this.jdbcTemplate.query(
                sqlQuery,
                (resultSet, rowNum) -> {
                    Film film = new Film();
                    film.setId(Integer.parseInt(resultSet.getString("FILM_ID")));
                    film.setName(resultSet.getString("FILM_NAME"));
                    film.setDescription(resultSet.getString("FILM_DESCRIPTION"));
                    film.setDuration(Integer.parseInt(resultSet.getString("FILM_DURATION")));
                    film.setReleaseDate(LocalDate.parse(resultSet.getString("FILM_RELEASE_DATE")));
                    film.setRate(Integer.parseInt(resultSet.getString("FILM_RATE")));
                    film.setMpa(new MPA(Integer.parseInt(resultSet.getString("FILM_MPA"))));
                    film.setMpa(new MPA(Integer.parseInt(resultSet.getString("FILM_MPA")), resultSet.getString("MPA_NAME")));
                    String likesStr = resultSet.getString("likes");
                    if (likesStr != null) {
                        Set<Integer> likes = Arrays.stream(likesStr.split(","))
                                .map(Integer::parseInt)
                                .collect(Collectors.toSet());
                        film.setLikes(likes);
                    }
                    return film;
                });
        for (Film film : allFilms) {
            if (film != null) {
                takeGenreFromDb(film);
            }
        }
        Map<Integer, Film> filmMap = allFilms.stream().collect(Collectors.toMap(Film::getId, film -> film));
        return (HashMap<Integer, Film>) filmMap;
    }

    @Override
    public int removeLikeFromRepo(int filmId, int userId) {
        String sqlQuery = "DELETE FROM LIKES WHERE FILM_ID=? AND USER_ID=?";
        return jdbcTemplate.update(sqlQuery, filmId, userId);
    }

    @Override
    public Film getFilmFromRepoById(int filmId) {

        String sqlQuery = "select " +
                "f.FILM_ID," +
                "f.FILM_NAME," +
                "f.FILM_DESCRIPTION," +
                "f.FILM_RELEASE_DATE," +
                "f.FILM_DURATION," +
                "f.FILM_RATE," +
                "f.FILM_MPA," +
                "m.mpa_name from Films f " +
                "inner join MPA m on m.mpa_id = f.FILM_MPA " +
                "where f.FILM_ID = ?";
        Film result = null;
        try {
            result = jdbcTemplate.queryForObject(
                    sqlQuery,
                    (resultSet, rowNum) -> {
                        Film newFilm = new Film();
                        newFilm.setId(Integer.parseInt(resultSet.getString("FILM_ID")));
                        newFilm.setName(resultSet.getString("FILM_NAME"));
                        newFilm.setDescription(resultSet.getString("FILM_DESCRIPTION"));
                        newFilm.setReleaseDate(LocalDate.parse(resultSet.getString("FILM_RELEASE_DATE")));
                        newFilm.setDuration(Integer.parseInt(resultSet.getString("FILM_DURATION")));
                        newFilm.setMpa(new MPA(Integer.parseInt(resultSet.getString("FILM_MPA"))));
                        newFilm.setMpa(new MPA(Integer.parseInt(resultSet.getString("FILM_MPA")), resultSet.getString("MPA_NAME")));
                        return newFilm;
                    },
                    filmId);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException(HttpStatus.NOT_FOUND, "Film not found");
        }
        return takeGenreFromDb(result);
    }

    private Film takeGenreFromDb(Film result) {
        String sqlQuery =
                "select fg.GENRE_ID," +
                        " g.GENRE_NAME from FILM_GENRES fg " +
                        "inner join GENRES g on fg.GENRE_ID = g.GENRE_ID " +
                        "where fg.FILM_ID = ?";
        List<Genre> foundGenres = jdbcTemplate.query(sqlQuery,
                new Object[]{result.getId()},
                new int[]{Types.INTEGER},
                (rs, rowNum) -> new Genre(rs.getInt("GENRE_ID"),
                        rs.getString("GENRE_NAME")));
        result.setGenres(foundGenres);
        return result;
    }
}