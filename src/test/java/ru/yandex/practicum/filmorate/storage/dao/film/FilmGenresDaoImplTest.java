package ru.yandex.practicum.filmorate.storage.dao.film;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.model.FilmGenre;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

@JdbcTest
@Sql(scripts = "/test_schema.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmGenresDaoImplTest {
	private final JdbcTemplate jdbc;
	private FilmGenresDaoImpl genresDao;

	@BeforeEach
	void createGenresDao() {
		genresDao = new FilmGenresDaoImpl(jdbc);
	}

	@Test
	void save_genres_test() {
		genresDao.save(
				Set.of(
						getDBSavedGenres().get(1),
						getDBSavedGenres().get(3)
				),
				4
		);
		List<Integer> expectedGenresId = List.of(2, 4);
		String sql = "SELECT * FROM films_genres WHERE film_id = 4;";
		List<Integer> actualGenresId = jdbc.query(sql, (rs, rowNum) -> rs.getInt("genre_id"));

		assertArrayEquals(expectedGenresId.toArray(), actualGenresId.toArray());
	}

	@Test
	void save_genres_already_added_test() {
		Exception exception = assertThrows(
				DuplicateKeyException.class,
				() -> genresDao.save(
						Set.of(
								getDBSavedGenres().get(0),
								getDBSavedGenres().get(3)
						),
						1
				)
		);

		String exceptedMessage = "Unique index or primary key violation";
		String actualMessage = exception.getMessage();

		assert actualMessage != null;
		assertTrue(actualMessage.contains(exceptedMessage));
	}

	@Test
	void get_genres_by_film_id_test() {
		Set<FilmGenre> exceptedGenres = Set.of(
				getDBSavedGenres().get(0),
				getDBSavedGenres().get(2)
		);
		Set<FilmGenre> actualGenres = genresDao.getByFilmId(1);

		assertArrayEquals(exceptedGenres.toArray(), actualGenres.toArray());
	}

	@Test
	void get_genres_by_films_id_test() {
		Map<Integer, Set<FilmGenre>> actualFilmGenres = genresDao.getByFilmsId(List.of(1,2));
		List<FilmGenre> firstActual = new ArrayList<>(actualFilmGenres.get(1));
		List<FilmGenre> secondActual = new ArrayList<>(actualFilmGenres.get(2));

		List<FilmGenre> firstExcepted = List.of(
				getDBSavedGenres().get(0),
				getDBSavedGenres().get(2)
		);
		List<FilmGenre> secondExpected = List.of(
				getDBSavedGenres().get(1)
		);

		assertArrayEquals(firstExcepted.toArray(), firstActual.toArray());
		assertArrayEquals(secondExpected.toArray(), secondActual.toArray());
	}

	@Test
	void get_all_test() {
		Map<Integer, Set<FilmGenre>> actualFilmGenres = genresDao.getAll();
		List<FilmGenre> firstActual = new ArrayList<>(actualFilmGenres.get(1));
		List<FilmGenre> secondActual = new ArrayList<>(actualFilmGenres.get(2));
		List<FilmGenre> thirdActual = new ArrayList<>(actualFilmGenres.get(3));

		List<FilmGenre> firstExcepted = List.of(
				getDBSavedGenres().get(0),
				getDBSavedGenres().get(2)
		);
		List<FilmGenre> secondExpected = List.of(
				getDBSavedGenres().get(1)
		);
		List<FilmGenre> thirdExpected = List.of(
				getDBSavedGenres().get(5),
				getDBSavedGenres().get(0),
				getDBSavedGenres().get(3)
		);

		assertArrayEquals(firstExcepted.toArray(), firstActual.toArray());
		assertArrayEquals(secondExpected.toArray(), secondActual.toArray());
		assertArrayEquals(thirdExpected.toArray(), thirdActual.toArray());
	}

	@Test
	void get_genres_by_genres_id() {
		assertArrayEquals(
				List.of(
						getDBSavedGenres().get(0),
						getDBSavedGenres().get(3)
				).toArray(),
				genresDao.get(Set.of(1, 4)).toArray()
		);
	}

	@Test
	void get_all_genres() {
		List<FilmGenre> genres = new ArrayList<>(genresDao.getAllGenres());
		genres.sort(Comparator.comparingInt(FilmGenre::getId));
		assertArrayEquals(getDBSavedGenres().toArray(), genres.toArray());
	}

	@Test
	void get_genre_by_genre_id() {
		assertEquals(getDBSavedGenres().get(2), genresDao.getGenre(3).get());
	}

	@Test
	void update_film_genre_test() {
		genresDao.update(
				1,
				new HashSet<>(getDBSavedGenres())
		);

		List<FilmGenre> genres = new ArrayList<>(genresDao.getByFilmId(1));
		genres.sort(Comparator.comparingInt(FilmGenre::getId));
		assertArrayEquals(getDBSavedGenres().toArray(), genres.toArray());
	}

	private List<FilmGenre> getDBSavedGenres() {
		return List.of(
				FilmGenre.builder()
						.id(1)
						.name("Комедия")
						.build(),
				FilmGenre.builder()
						.id(2)
						.name("Драма")
						.build(),
				FilmGenre.builder()
						.id(3)
						.name("Мультфильм")
						.build(),
				FilmGenre.builder()
						.id(4)
						.name("Триллер")
						.build(),
				FilmGenre.builder()
						.id(5)
						.name("Документальный")
						.build(),
				FilmGenre.builder()
						.id(6)
						.name("Боевик")
						.build()
		);
	}
}