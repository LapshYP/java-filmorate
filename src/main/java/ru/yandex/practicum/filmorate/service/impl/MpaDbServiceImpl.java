package ru.yandex.practicum.filmorate.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.yandex.practicum.filmorate.dao.MpaStorage;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.HashMap;

@Slf4j
@Service
@Validated
@Primary
@RequiredArgsConstructor
public class MpaDbServiceImpl implements MpaService {

    private final MpaStorage mpaStorage;

    @Override
    public HashMap<Integer, MPA> getAllMpas() {
        return mpaStorage.getMpasFromRepo();
    }

    @Override
    public MPA getMpaById(int mpaId) {
        return mpaStorage.getMpaFromRepoById(mpaId);
    }


}
