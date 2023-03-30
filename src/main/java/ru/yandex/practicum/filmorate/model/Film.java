package ru.yandex.practicum.filmorate.model;

import lombok.*;
import ru.yandex.practicum.filmorate.validator.ValidDate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Data //Equivalent to @Getter @Setter @RequiredArgsConstructor @ToString @EqualsAndHashCode
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
  }
