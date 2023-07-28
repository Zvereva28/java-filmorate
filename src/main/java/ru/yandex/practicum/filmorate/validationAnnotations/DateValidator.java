package ru.yandex.practicum.filmorate.validationAnnotations;

import lombok.val;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;

public class DateValidator implements ConstraintValidator<InMovieEpoch, LocalDate> {
    @Override
    public void initialize(InMovieEpoch constraintAnnotation) {
    }

    @Override
    public boolean isValid(LocalDate date, ConstraintValidatorContext context) {
        if (date == null) {
            return false;
        }
        val filmsEpochBegin = LocalDate.of(1895, 11, 28);

        return date.isAfter(filmsEpochBegin);
    }
}
