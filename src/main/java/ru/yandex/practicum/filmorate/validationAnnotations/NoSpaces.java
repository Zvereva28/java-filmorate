package ru.yandex.practicum.filmorate.validationAnnotations;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import  ru.yandex.practicum.filmorate.validatorAnnotations.spacesValidatorAnnotations.SpacesValidator;

@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER,
        ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = SpacesValidator.class)
@Documented
public @interface NoSpaces {
    String message() default "Ошибка в данных юзера, логин не должен содержать пробелы: ${validatedValue}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
