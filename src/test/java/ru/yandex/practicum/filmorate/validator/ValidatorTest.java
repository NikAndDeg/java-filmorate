package ru.yandex.practicum.filmorate.validator;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class ValidatorTest {
	@Autowired
	private Validator validator;
	User defaultUser = User.builder()
			.id(1)
			.email("some@email.com")
			.login("SomeLogin")
			.name("SomeName")
			.birthday(LocalDate.of(2011, 11, 11))
			.build();
	User user;

	@Test
	void user_not_valid_birth_now() {
		user = defaultUser.toBuilder()
				.birthday(LocalDate.now())
				.build();
		assertEquals("Invalid birth date!", validateUser(user).get(0).getMessage());
	}

	@Test
	void user_not_valid_birth_future() {
		user = defaultUser.toBuilder()
				.birthday(LocalDate.now().plusYears(1))
				.build();
		assertEquals("Invalid birth date!", validateUser(user).get(0).getMessage());
	}

	@Test
	void user_not_valid_login_empty() {
		user = defaultUser.toBuilder()
				.login(null)
				.build();
		assertEquals("Invalid login!", validateUser(user).get(0).getMessage());
	}

	@Test
	void user_not_valid_login_blank() {
		user = defaultUser.toBuilder()
				.login("   ")
				.build();
		assertEquals("Invalid login!", validateUser(user).get(0).getMessage());
	}

	@Test
	void user_not_valid_login_with_blank() {
		user = defaultUser.toBuilder()
				.login("Some Login")
				.build();
		assertEquals("Invalid login!", validateUser(user).get(0).getMessage());
	}

	@Test
	void film_not_valid_release_date_before_1986_12_28() {
		Film film = Film.builder()
				.name("SomeName")
				.description("Some Description")
				.releaseDate(LocalDate.of(1895, 12, 27))
				.duration(1)
				.build();
		assertEquals("Invalid release date!", validateFilm(film).get(0).getMessage());
	}

	@Test
	void film_valid_release_date1986_12_28() {
		Film film = Film.builder()
				.name("SomeName")
				.description("Some Description")
				.releaseDate(LocalDate.of(1895, 12, 28))
				.duration(1)
				.build();
		assertEquals("Invalid release date!", validateFilm(film).get(0).getMessage());
	}

	private List<ConstraintViolation<User>> validateUser(User user) {
		return new ArrayList<>(validator.validate(user));
	}

	private List<ConstraintViolation<Film>> validateFilm(Film film) {
		return new ArrayList<>(validator.validate(film));
	}
}