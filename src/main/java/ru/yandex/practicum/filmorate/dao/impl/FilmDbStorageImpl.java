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
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@AllArgsConstructor
@Primary
public class FilmDbStorageImpl implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Film addFilmLikeToRepo(Film filmToLike, int userId) {

        KeyHolder keyHolder = new GeneratedKeyHolder();

        String sqlQuery = "insert into LIKES (FILM_ID, USER_ID)" +
                "values (?, ?)";

        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery);
            stmt.setLong(1, filmToLike.getId());
            stmt.setLong(2, userId);
            return stmt;
        }, keyHolder);

        return filmToLike;
    }

    @Override
    public Film addFilmToRepo(Film film) {
        String sqlQuery = "insert into FILMS (FILM_NAME, FILM_DESCRIPTION, FILM_RELEASE_DATE, FILM_DURATION, FILM_RATE, FILM_MPA ) values (?, ?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"film_id"});
            stmt.setString(1, film.getName());
            stmt.setString(2, film.getDescription());
            stmt.setDate(3, Date.valueOf(film.getReleaseDate()));
            stmt.setInt(4, film.getDuration());
            stmt.setInt(5, film.getRate());
            stmt.setInt(6, film.getMpa().getId());
            return stmt;
        }, keyHolder);

        if (film.getGenres() != null) {
            addGenres(film);
        } else if (film.getGenres() == null) {
            ArrayList<Genre> genreList = new ArrayList<>();
            genreList.add(new Genre(0, null));
            film.setGenres(genreList);
        }
        return film;
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