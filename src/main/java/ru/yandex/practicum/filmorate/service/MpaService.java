package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.MPA;

import java.util.HashMap;

@Service
public interface MpaService {

    HashMap<Integer, MPA> getAllMpas();

    MPA getMpaById(int mpaId);

}
