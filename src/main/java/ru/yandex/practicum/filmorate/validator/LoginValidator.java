package ru.yandex.practicum.filmorate.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

class LoginValidator implements ConstraintValidator<LoginValidation, String> {
	@Override
	public boolean isValid(String contactField,
						   ConstraintValidatorContext cxt) {
		if (contactField == null)
			return false;
		if (contactField.isEmpty() || contactField.isBlank())
			return false;
		if (contactField.contains(" "))
			return false;
		return true;
	}
}
