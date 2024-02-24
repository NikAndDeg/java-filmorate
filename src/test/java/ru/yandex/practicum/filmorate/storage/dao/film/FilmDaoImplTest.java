package ru.yandex.practicum.filmorate.storage.dao.film;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Sql(scripts = "/FilmDaoImplTest_schema.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class FilmDaoImplTest {
	@Autowired
	private FilmDaoImpl filmDao;

	private final Film defaultFilm = Film.builder()
			.name("Film")
			.description("Description")
			.releaseDate(LocalDate.of(2000, 01, 01))
			.duration(123)
			.build();

	private Film actual;
	private Film expected;

	@Test
	void save_film() {
		expected = defaultFilm.toBuilder()
				.id(1)
				.build();
		actual = filmDao.save(defaultFilm);
		assertFilmEquals(expected, actual);
	}

	@Test
	void get_film_by_id() {
		filmDao.save(defaultFilm);

		expected = defaultFilm.toBuilder()
				.id(1)
				.build();
		actual = filmDao.get(1).get();
		assertFilmEquals(expected, actual);
	}

	@Test
	void get_nonexistent_film_id_999() {
		assertTrue(filmDao.get(999).isEmpty());
	}

	@Test
	void get_all_films() {
		filmDao.save(defaultFilm);
		filmDao.save(
				defaultFilm.toBuilder()
						.name("Another Film")
						.build()
		);

		List<Film> actualFilms = filmDao.getAll();

		assertFilmEquals(
				defaultFilm.toBuilder()
						.id(1)
						.build(),
				actualFilms.get(0)
		);
		assertFilmEquals(
				defaultFilm.toBuilder()
						.name("Another Film")
						.id(2)
						.build(),
				actualFilms.get(1)
		);
	}

	@Test
	void get_films_by_id_1_3() {
		filmDao.save(defaultFilm);
		filmDao.save(
				defaultFilm.toBuilder()
						.name("Another Film")
						.build()
		);
		filmDao.save(
				defaultFilm.toBuilder()
						.name("Third Film")
						.build()
		);

		List<Film> actualFilms = filmDao.get(List.of(1, 3));

		assertFilmEquals(
				defaultFilm.toBuilder()
						.id(1)
						.build(),
				actualFilms.get(0)
		);
		assertFilmEquals(
				defaultFilm.toBuilder()
						.name("Third Film")
						.id(3)
						.build(),
				actualFilms.get(1)
		);
	}

	@Test
	void remove_film_by_id() {
		filmDao.save(defaultFilm);
		expected = defaultFilm.toBuilder()
				.id(1)
				.build();
		actual = filmDao.remove(1).get();
		assertFilmEquals(expected, actual);
	}

	@Test
	void delete_nonexistent_film_id_999() {
		assertTrue(filmDao.remove(999).isEmpty());
	}

	@Test
	void update_film() {
		filmDao.save(defaultFilm);
		expected = defaultFilm.toBuilder()
				.id(1)
				.name("Updated Film")
				.duration(222)
				.build();
		actual = filmDao.update(
				defaultFilm.toBuilder()
						.id(1)
						.name("Updated Film")
						.duration(222)
						.build()
		);
		assertFilmEquals(expected, actual);
	}

	@Test
	void contains_film() {
		filmDao.save(defaultFilm);
		assertTrue(filmDao.contains(defaultFilm.toBuilder().id(1).build()));
	}

	@Test
	void contains_nonexistent_film() {
		assertFalse(filmDao.contains(defaultFilm));
	}

	private void assertFilmEquals(Film expected, Film actual) {
		assertEquals(expected.getId(), actual.getId());
		assertEquals(expected.getName(), actual.getName());
		assertEquals(expected.getDescription(), actual.getDescription());
		assertEquals(expected.getReleaseDate(), actual.getReleaseDate());
		assertEquals(expected.getDuration(), actual.getDuration());
		if (expected.getLikes() != null)
			assertArrayEquals(expected.getLikes().toArray(), actual.getLikes().toArray());
	}
}