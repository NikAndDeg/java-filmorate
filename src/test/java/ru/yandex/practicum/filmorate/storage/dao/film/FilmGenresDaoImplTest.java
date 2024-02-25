package ru.yandex.practicum.filmorate.storage.dao.film;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.model.FilmGenre;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Sql(scripts = "/test_schema.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class FilmGenresDaoImplTest {
	@Autowired
	private FilmGenresDaoImpl genresDao;

	private final List<FilmGenre> genres = List.of(
			FilmGenre.builder().id(1).name("Комедия").build(),
			FilmGenre.builder().id(2).name("Драма").build(),
			FilmGenre.builder().id(3).name("Мультфильм").build(),
			FilmGenre.builder().id(4).name("Триллер").build(),
			FilmGenre.builder().id(5).name("Документальный").build(),
			FilmGenre.builder().id(6).name("Боевик").build()
	);

	@Test
	void get_all_genres() {
		System.out.println(genresDao.getAll());
		assertArrayEquals(genres.toArray(), genresDao.getAllGenres().toArray());

	}

	@Test
	void get_genres_by_genres_id() {
		assertArrayEquals(
				Set.of(genres.get(1), genres.get(2), genres.get(3)).toArray(),
				genresDao.get(Set.of(2, 3, 4)).toArray()
		);
	}

	@Test
	void get_genre_by_id() {
		assertEquals(genres.get(0), genresDao.getGenre(1).get());
	}
}