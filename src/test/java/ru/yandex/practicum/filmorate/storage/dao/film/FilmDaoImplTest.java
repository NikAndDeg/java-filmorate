package ru.yandex.practicum.filmorate.storage.dao.film;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.MPARating;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@Sql(scripts = "/test_schema.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmDaoImplTest {
	private final JdbcTemplate jdbc;
	private FilmDaoImpl filmDao;

	@BeforeEach
	void createFilmDao() {
		filmDao = new FilmDaoImpl(jdbc);
	}

	@Test
	void save_film_test() {
		Film expected = Film.builder()
				.id(5)
				.name("5 Film")
				.description("Description")
				.releaseDate(LocalDate.parse("2005-05-05"))
				.duration(123)
				.mpa(
						MPARating.builder()
								.id(5)
								.name("NC-17")
								.build()
				)
				.build();

		Film actual = filmDao.save(
				Film.builder()
						.name("5 Film")
						.description("Description")
						.releaseDate(LocalDate.parse("2005-05-05"))
						.duration(123)
						.mpa(
								MPARating.builder()
										.id(5)
										.name("NC-17")
										.build()
						)
						.build()
		);

		assertFilmsEquals(expected, actual);
	}

	@Test
	void get_film_by_id_test() {
		Film expected = getDBSavedFilms().get(2);
		Film actual = filmDao.get(3).get();
		assertFilmsEquals(expected, actual);
	}

	@Test
	void get_by_id_nonexistent_film_test() {
		Optional<Film> notFoundedFilm = filmDao.get(999);
		assertTrue(notFoundedFilm.isEmpty());
	}

	@Test
	void get_by_list_of_id() {
		List<Film> expectedFilms = List.of(
				getDBSavedFilms().get(0),
				getDBSavedFilms().get(2)
		);
		List<Film> actualFilms = filmDao.get(List.of(1, 3));
		assertFilmsEquals(expectedFilms.get(0), actualFilms.get(0));
		assertFilmsEquals(expectedFilms.get(1), actualFilms.get(1));
	}

	@Test
	void get_by_list_of_id_with_nonexistent_film() {
		List<Film> expectedFilms = List.of(
				getDBSavedFilms().get(0),
				getDBSavedFilms().get(2)
		);
		List<Film> actualFilms = filmDao.get(List.of(1, 3, 999));
		assertFilmsEquals(expectedFilms.get(0), actualFilms.get(0));
		assertFilmsEquals(expectedFilms.get(1), actualFilms.get(1));
	}

	@Test
	void get_all_test() {
		assertArrayEquals(getDBSavedFilms().toArray(), filmDao.getAll().toArray());
	}

	@Test
	void remove_test() {
		Film expected = getDBSavedFilms().get(0);
		Film actual = filmDao.remove(1).get();
		assertFilmsEquals(expected, actual);
		assertTrue(filmDao.get(1).isEmpty());
	}

	@Test
	void remove_nonexistent_film_test() {
		Film expected = getDBSavedFilms().get(0);
		Film actual = filmDao.remove(1).get();
		assertFilmsEquals(expected, actual);
		assertTrue(filmDao.get(1).isEmpty());
	}

	@Test
	void update_film_test() {
		Film expected = getDBSavedFilms().get(0).toBuilder()
				.name("Updated Name")
				.build();
		Film actual = filmDao.update(
				getDBSavedFilms().get(0).toBuilder()
						.name("Updated Name")
						.build()
		);
		assertFilmsEquals(expected, actual);
		actual = filmDao.get(1).get();
		assertFilmsEquals(expected, actual);
	}

	@Test
	void update_nonexistent_film_test() {
		Film nonexistentFilm = getDBSavedFilms().get(0).toBuilder()
						.id(999)
						.name("Updated Name")
						.build();

		Exception exception = assertThrows(
				NoSuchElementException.class,
				() -> filmDao.update(nonexistentFilm)
		);

		String expectedMessage = "No value present";
		String actualMessage = exception.getMessage();

		assertTrue(actualMessage.contains(expectedMessage));
	}

	@Test
	void contains_by_object_film_test() {
		Film film = getDBSavedFilms().get(0);
		assertTrue(filmDao.contains(film));
	}

	@Test
	void contains_by_object_nonexistent_film_test() {
		Film nonexistentFilm = getDBSavedFilms().get(0).toBuilder()
				.name("Nonexistent Film")
				.build();

		assertFalse(filmDao.contains(nonexistentFilm));
	}

	@Test
	void contains_by_film_id_test() {
		assertTrue(filmDao.contains(1));
	}

	@Test
	void contains_by_nonexistent_film_id_test() {
		assertFalse(filmDao.contains(999));
	}

	private void assertFilmsEquals(Film expected, Film actual) {
		assertEquals(expected.getId(), actual.getId());
		assertEquals(expected.getName(), actual.getName());
		assertEquals(expected.getDescription(), actual.getDescription());
		assertEquals(expected.getReleaseDate(), actual.getReleaseDate());
		assertEquals(expected.getDuration(), actual.getDuration());
		assertEquals(expected.getMpa(), actual.getMpa());
	}

	private List<Film> getDBSavedFilms() {
		return List.of(
				Film.builder()
						.id(1)
						.name("1 Film")
						.description("Description")
						.releaseDate(LocalDate.parse("2001-01-01"))
						.duration(123)
						.mpa(
								MPARating.builder()
										.id(1)
										.name("G")
										.build()
						)
						.build(),
				Film.builder()
						.id(1)
						.name("2 Film")
						.description("Description")
						.releaseDate(LocalDate.parse("2002-02-02"))
						.duration(123)
						.mpa(
								MPARating.builder()
										.id(2)
										.name("PG")
										.build()
						)
						.build(),
				Film.builder()
						.id(3)
						.name("3 Film")
						.description("Description")
						.releaseDate(LocalDate.parse("2003-03-03"))
						.duration(123)
						.mpa(
								MPARating.builder()
										.id(3)
										.name("PG-13")
										.build()
						)
						.build(),
				Film.builder()
						.id(4)
						.name("4 Film")
						.description("Description")
						.releaseDate(LocalDate.parse("2004-04-04"))
						.duration(123)
						.mpa(
								MPARating.builder()
										.id(4)
										.name("R")
										.build()
						)
						.build()
				);
	}
}