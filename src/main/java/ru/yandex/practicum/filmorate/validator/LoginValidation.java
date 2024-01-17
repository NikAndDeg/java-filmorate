package ru.yandex.practicum.filmorate.validator;

import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;

@Documented
@Constraint(validatedBy = LoginValidator.class)
@Target({FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface LoginValidation {
	String message() default "Invalid login!";
	Class<?>[] groups() default {};
	Class<? extends Payload>[] payload() default {};
}

class LoginValidator implements ConstraintValidator<LoginValidation, String> {
	@Override
	public boolean isValid(String contactField,
						   ConstraintValidatorContext cxt) {
		if (contactField == null)
			return false;
		if (contactField.isEmpty() || contactField. isBlank())
			return false;
		if (contactField.contains(" "))
			return false;
		return true;
	}
}
