package ru.yandex.practicum.filmorate.validator;

import javax.validation.Constraint;
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

