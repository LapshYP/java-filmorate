package ru.yandex.practicum.filmorate.model;

import lombok.*;
import ru.yandex.practicum.filmorate.validator.ValidName;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class User {

    int id;
    @Email
    String email;
    @NotBlank
    String login;
    @ValidName
    String name;
    @PastOrPresent
    LocalDate birthday;
    Set<Integer> friends = new HashSet<>();

}
