package ru.yandex.practicum.filmorate.validator;

import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Target;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.time.LocalDate;

import static java.lang.annotation.ElementType.*;

@Documented
@Constraint(validatedBy = BirthDateValidator.class)
@Target({FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface BirthDateValidation {
	String message() default "Invalid birth date!";
	Class<?>[] groups() default {};
	Class<? extends Payload>[] payload() default {};
}

