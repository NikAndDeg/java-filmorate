package ru.yandex.practicum.filmorate.validator;

import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.time.LocalDate;

import static java.lang.annotation.ElementType.*;

@Documented
@Constraint(validatedBy = ReleaseDateValidator.class)
@Target({FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ReleaseDateValidation {
	String message() default "Invalid release date!";
	Class<?>[] groups() default {};
	Class<? extends Payload>[] payload() default {};
}

class ReleaseDateValidator implements ConstraintValidator<ReleaseDateValidation, LocalDate> {
	private static final LocalDate FIRST_FILM_RELEASE_DATE = LocalDate.of(1895, 12, 28);
	@Override
	public boolean isValid(LocalDate contactField,
						   ConstraintValidatorContext cxt) {
		return contactField.isAfter(FIRST_FILM_RELEASE_DATE);
	}
}
