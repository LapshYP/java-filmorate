package ru.yandex.practicum.filmorate.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class NameValidator implements ConstraintValidator<ValidName, String> {
    @Override
    public void initialize(ValidName constraintAnnotation) {
    }
    @Override
    public boolean isValid(String username, ConstraintValidatorContext constraintValidatorContext) {
        if (username==null || username.equals(""))
        {
            return true;
        } else return username.matches("^(?=\\s*\\S).*$");
    }
}