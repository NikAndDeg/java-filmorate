package ru.yandex.practicum.filmorate.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;

class ReleaseDateValidator implements ConstraintValidator<ReleaseDateValidation, LocalDate> {
	private static final LocalDate FIRST_FILM_RELEASE_DATE = LocalDate.of(1895, 12, 28);

	@Override
	public boolean isValid(LocalDate contactField,
						   ConstraintValidatorContext cxt) {
		return contactField.isAfter(FIRST_FILM_RELEASE_DATE);
	}
}
