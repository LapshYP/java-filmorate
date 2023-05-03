package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.GenreStorage;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@Primary
@RequiredArgsConstructor
public class GenreDbStorageImpl implements GenreStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Genre getGenreFromRepoById(int genreId) {

        String sqlQuery = "SELECT genre_id, genre_name FROM GENRES WHERE genre_id = " + genreId;
        Genre result = null;
        try {
            result = jdbcTemplate.queryForObject(
                    sqlQuery,
                    (resultSet, rowNum) -> {
                        Genre newGenre = new Genre();
                        newGenre.setId(Integer.parseInt(resultSet.getString("GENRE_ID")));
                        newGenre.setName(resultSet.getString("GENRE_NAME"));
                        return newGenre;
                    });
        } catch (EmptyResultDataAccessException e) {
            // обработка ситуации, когда пользователя с указанным ID не было найдено
            throw new NotFoundException(HttpStatus.NOT_FOUND, "Genre not found");
        }
        return result;
    }

    @Override
    public HashMap<Integer, Genre> getGenresFromRepo() {
        List<Genre> allGenres = this.jdbcTemplate.query(
                "SELECT genre_id, genre_name FROM GENRES ",
                (resultSet, rowNum) -> {
                    Genre genre = new Genre();
                    genre.setId(Integer.parseInt(resultSet.getString("GENRE_ID")));
                    genre.setName(resultSet.getString("GENRE_NAME"));
                    return genre;
                });
        Map<Integer, Genre> allGenresMap = allGenres.stream().collect(Collectors.toMap(Genre::getId, genre -> genre));

        return (HashMap<Integer, Genre>) allGenresMap;
    }
}
