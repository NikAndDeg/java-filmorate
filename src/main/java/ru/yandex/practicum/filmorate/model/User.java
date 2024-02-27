package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import ru.yandex.practicum.filmorate.validator.BirthDateValidation;
import ru.yandex.practicum.filmorate.validator.LoginValidation;

import javax.validation.constraints.Email;

import java.time.LocalDate;
import java.util.Set;

@Data
@Builder(toBuilder = true)
public class User {
	@EqualsAndHashCode.Exclude
	private int id;
	@Email
	private String email;
	@LoginValidation
	private String login;
	@EqualsAndHashCode.Exclude
	private String name;
	@EqualsAndHashCode.Exclude
	@BirthDateValidation
	private LocalDate birthday;
	@EqualsAndHashCode.Exclude
	private Set<Integer> friends;
}
