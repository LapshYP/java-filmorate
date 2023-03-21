package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserServiceImpl;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    UserServiceImpl userServiceImpl = new UserServiceImpl();


    @GetMapping
    public List<User> getAllUser() {
        log.debug("There is {} user in filmorate", userServiceImpl.getAllUsers().size());
        return userServiceImpl.getAllUsers()
                .values()
                .stream()
                .collect(Collectors.toList());
    }

    @PostMapping
    public @Valid User addUser(@Valid @RequestBody User user) {

        @Valid User userToAdd = userServiceImpl.addUsers(user);
        log.debug("user with name = \"{}\"  added", user.getName());
        return userToAdd;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {

        @Valid User updetedUser = userServiceImpl.updateUsers(user);
        log.debug("user with name = \"{}\" updated", user.getName());
        return updetedUser;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }
}
