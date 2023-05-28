package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/mpa")
@RequiredArgsConstructor
public class MpaController {

    public final MpaService mpaService;

    @GetMapping
    public List<MPA> getAllMpa() {
        var mpas = mpaService.getAllMpas();
        List<MPA> mpaList = new ArrayList<>(mpas.values());
        log.debug("There are {} users in filmorate", mpaService.getAllMpas().size());
        return mpaList;
    }

    @GetMapping("/{mpaId}")
    public MPA findMpaById(@PathVariable int mpaId) {
        MPA mpaById = mpaService.getMpaById(mpaId);
        log.debug("MPA with id = \"{}\"  ", mpaId);
        return mpaById;
    }

}
