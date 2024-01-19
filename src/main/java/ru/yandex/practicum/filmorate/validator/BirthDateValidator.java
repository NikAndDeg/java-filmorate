package ru.yandex.practicum.filmorate.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;

class BirthDateValidator implements ConstraintValidator<BirthDateValidation, LocalDate> {
	public void initialize(BirthDateValidator birthDateValidator) {
	}

	@Override
	public boolean isValid(LocalDate contactField,
						   ConstraintValidatorContext cxt) {
		return contactField.isBefore(LocalDate.now());
	}
}
