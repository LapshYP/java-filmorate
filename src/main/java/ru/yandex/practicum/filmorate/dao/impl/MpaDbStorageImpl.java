package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.MpaStorage;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.MPA;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@Primary
@RequiredArgsConstructor
public class MpaDbStorageImpl implements MpaStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public MPA getMpaFromRepoById(int mpaId) {

        String sqlQuery = "SELECT mpa_id, mpa_name FROM MPA WHERE mpa_id = " + mpaId;
        MPA result;
        try {
            result = jdbcTemplate.queryForObject(
                    sqlQuery,
                    (resultSet, rowNum) -> {
                        MPA newMpa = new MPA();
                        newMpa.setId(Integer.parseInt(resultSet.getString("MPA_ID")));
                        newMpa.setName(resultSet.getString("MPA_NAME"));
                        return newMpa;
                    });
        } catch (EmptyResultDataAccessException e) {
            // обработка ситуации, когда пользователя с указанным ID не было найдено
            throw new NotFoundException(HttpStatus.NOT_FOUND, "MPA not found");
        }
        return result;
    }

    @Override
    public HashMap<Integer, MPA> getMpasFromRepo() {
        List<MPA> allMpas = this.jdbcTemplate.query(
                "SELECT mpa_id, mpa_name FROM MPA",
                (resultSet, rowNum) -> {
                    MPA mpa = new MPA();
                    mpa.setId(Integer.parseInt(resultSet.getString("MPA_ID")));
                    mpa.setName(resultSet.getString("MPA_NAME"));
                    return mpa;
                });
        Map<Integer, MPA> allMpasMap = allMpas.stream().collect(Collectors.toMap(MPA::getId, mpa -> mpa));

        return (HashMap<Integer, MPA>) allMpasMap;
    }
}
