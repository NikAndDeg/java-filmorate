package ru.yandex.practicum.filmorate.storage.dao.film;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@Sql(scripts = "/test_schema.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class FilmLikesDaoImplTest {
	@Autowired
	private FilmLikesDaoImpl likesDao;

	@Test
	void get_most_liked_films() {
		likesDao.add(1, 3);
		likesDao.add(1, 2);

		likesDao.add(2, 1);
		likesDao.add(2, 2);
		likesDao.add(2, 3);

		likesDao.add(3, 1);

		System.out.println(likesDao.getMostLikedFilmsId(3));
	}

	@Test
	void remove_like() {
		likesDao.add(1, 3);
		likesDao.add(1, 2);
		likesDao.add(1, 1);

		likesDao.remove(1, 3);

		assertArrayEquals(List.of(1, 2).toArray(), likesDao.get(1).toArray());
	}

	@Test
	void get_likes_of_films_list() {
		likesDao.add(1, 3);
		likesDao.add(1, 2);

		likesDao.add(2, 1);
		likesDao.add(2, 2);
		likesDao.add(2, 3);

		likesDao.add(3, 1);

		Map<Integer, Set<Integer>> filmsLikes = likesDao.get(List.of(2, 3));

		assertArrayEquals(List.of(1, 2, 3).toArray(), filmsLikes.get(2).toArray());
		assertArrayEquals(List.of(1).toArray(), filmsLikes.get(3).toArray());
	}

	@Test
	void save_likes() {
		likesDao.save(2, Set.of(2, 3));
		assertArrayEquals(List.of(2, 3).toArray(), likesDao.get(2).toArray());
	}

	@Test
	void add_and_get_likes() {
		likesDao.add(1, 3);
		likesDao.add(1, 2);
		assertArrayEquals(List.of(2, 3).toArray(), likesDao.get(1).toArray());
	}

	@Test
	void get_empty_likes() {
		assertTrue(likesDao.get(1).isEmpty());
	}

	@Test
	void get_all_likes() {
		likesDao.add(1, 3);
		likesDao.add(1, 2);

		likesDao.add(2, 1);
		likesDao.add(2, 2);
		likesDao.add(2, 3);

		likesDao.add(3, 1);


		Map<Integer, Set<Integer>> filmsLikes = likesDao.getAll();

		assertArrayEquals(List.of(2, 3).toArray(), filmsLikes.get(1).toArray());
		assertArrayEquals(List.of(1, 2, 3).toArray(), filmsLikes.get(2).toArray());
		assertArrayEquals(List.of(1).toArray(), filmsLikes.get(3).toArray());
	}
}