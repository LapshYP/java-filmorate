package ru.yandex.practicum.filmorate.validator;

import ru.yandex.practicum.filmorate.validator.iml.NameValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = NameValidator.class)
@Documented
public @interface ValidName {

    String message() default "Не правильный формат имени";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

