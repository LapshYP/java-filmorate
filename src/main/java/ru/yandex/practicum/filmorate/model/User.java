package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDate;
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
    String name;
    @PastOrPresent
    LocalDate birthday;
    @Builder.Default
    Set<Integer> friends = new TreeSet<>();

}
