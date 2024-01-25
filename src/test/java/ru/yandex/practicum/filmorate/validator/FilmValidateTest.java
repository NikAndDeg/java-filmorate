package ru.yandex.practicum.filmorate.validator;

import net.bytebuddy.utility.RandomString;
import ru.yandex.practicum.filmorate.model.Film;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class FilmValidateTest {

	@Autowired
	private Validator validator;

	Film defaultFilm = Film.builder()
			.name("Some Name")
			.description("Some Description")
			.releaseDate(LocalDate.of(2000, 12, 28))
			.duration(1)
			.build();

	Film film;

	@Test
	void film_not_valid_duration_negative() {
		film = defaultFilm.toBuilder()
				.duration(-1)
				.build();
		assertEquals("must be greater than 0", validateFilm(film).get(0).getMessage());
	}

	@Test
	void film_not_valid_empty_name() {
		film = defaultFilm.toBuilder()
				.name(null)
				.build();
		assertEquals("must not be blank", validateFilm(film).get(0).getMessage());
	}

	@Test
	void film_not_valid_name_size_201() {
		film = defaultFilm.toBuilder()
				.name(RandomString.make(201))
				.build();
		assertEquals("size must be between 0 and 200", validateFilm(film).get(0).getMessage());
	}

	@Test
	void film_not_valid_description_size_201() {
		film = defaultFilm.toBuilder()
				.description(RandomString.make(300))
				.build();
		assertEquals("size must be between 0 and 200", validateFilm(film).get(0).getMessage());
	}

	@Test
	void film_not_valid_release_date_before_1986_12_28() {
		film = defaultFilm.toBuilder()
				.releaseDate(LocalDate.of(1895, 12, 27))
				.build();
		assertEquals("Invalid release date!", validateFilm(film).get(0).getMessage());
	}

	private List<ConstraintViolation<Film>> validateFilm(Film film) {
		return new ArrayList<>(validator.validate(film));
	}
}
