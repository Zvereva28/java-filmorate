package ru.yandex.practicum.filmorate.validatorAnnotations.spacesValidatorAnnotations;

import ru.yandex.practicum.filmorate.validationAnnotations.NoSpaces;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SpacesValidator implements ConstraintValidator<NoSpaces, String> {
    @Override
    public void initialize(NoSpaces constraintAnnotation) {
    }

    @Override
    public boolean isValid(String login, ConstraintValidatorContext context) {
        if (login == null) {
            return false;
        }

        Pattern pattern = Pattern.compile("\\s");
        Matcher matcher = pattern.matcher(login);
        return !matcher.find();
    }
}
