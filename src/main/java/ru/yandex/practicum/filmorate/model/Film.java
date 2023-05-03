package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.validator.ValidDate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Film {
    int id;
    @NotBlank
    String name;
    @Size(min = 1, max = 200, message = "{максимальная длина описания — 200 символов}")
    String description;
    @ValidDate
    LocalDate releaseDate;
    @Positive
    int duration;
    @Builder.Default
    Set<Integer> likes = new TreeSet<>();
    int rate;
    @Builder.Default
    List<Genre> genres = new ArrayList<>();
    MPA mpa;

}
