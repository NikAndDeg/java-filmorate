package ru.yandex.practicum.filmorate.storage.dao.film;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@Sql(scripts = "/test_schema.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmLikesDaoImplTest {
	private final JdbcTemplate jdbc;
	private FilmLikesDaoImpl likesDao;

	@BeforeEach
	void createLikesDao() {
		likesDao = new FilmLikesDaoImpl(jdbc);
	}

	@Test
	void save_likes_test() {
		likesDao.save(4, Set.of(1, 2, 3, 4));
		List<Integer> actualLikes = jdbc.query(
						"SELECT * FROM films_likes WHERE film_id = 4;",
						(rs, rowNum) -> rs.getInt("user_id")
						);
		List<Integer> expectedLikes = List.of(1, 2, 3, 4);
		assertArrayEquals(expectedLikes.toArray(), actualLikes.toArray());
	}

	@Test
	void get_likes_by_film_id_test() {
		List<Integer> actualLikes = new ArrayList<>(likesDao.get(1));
		List<Integer> expectedLikes = List.of(2, 3);
		assertArrayEquals(expectedLikes.toArray(), actualLikes.toArray());
	}

	@Test
	void get_likes_by_films_id_list_test() {
		Map<Integer, Set<Integer>> actualLikes = likesDao.get(List.of(1, 3));
		List<Integer> firstActualLikes = new ArrayList<>(actualLikes.get(1));
		List<Integer> secondActualLikes = new ArrayList<>(actualLikes.get(3));

		List<Integer> firstExpectedLikes = List.of(2, 3);
		List<Integer> secondExpectedLikes = List.of(1);

		assertArrayEquals(firstExpectedLikes.toArray(), firstActualLikes.toArray());
		assertArrayEquals(secondExpectedLikes.toArray(), secondActualLikes.toArray());
	}

	@Test
	void get_all_test() {
		Map<Integer, Set<Integer>> actualLikes = likesDao.getAll();
		List<Integer> firstActualLikes = new ArrayList<>(actualLikes.get(1));
		List<Integer> secondActualLikes = new ArrayList<>(actualLikes.get(3));
		List<Integer> thirdActualLikes = new ArrayList<>(actualLikes.get(2));

		List<Integer> firstExpectedLikes = List.of(2, 3);
		List<Integer> secondExpectedLikes = List.of(1);
		List<Integer> thirdExpectedLikes = List.of(1, 3);

		assertArrayEquals(firstExpectedLikes.toArray(), firstActualLikes.toArray());
		assertArrayEquals(secondExpectedLikes.toArray(), secondActualLikes.toArray());
		assertArrayEquals(thirdExpectedLikes.toArray(), thirdActualLikes.toArray());
		assertArrayEquals(List.of(1, 2, 3).toArray(), actualLikes.keySet().toArray());
	}

	@Test
	void add_like_test() {
		likesDao.add(4, 1);
		List<Integer> actualLikes = jdbc.query(
				"SELECT * FROM films_likes WHERE film_id = 4;",
				(rs, rowNum) -> rs.getInt("user_id")
		);
		List<Integer> expectedLikes = List.of(1);
		assertArrayEquals(expectedLikes.toArray(), actualLikes.toArray());
	}

	@Test
	void remove_like_test() {
		likesDao.remove(1, 2);
		List<Integer> actualLikes = jdbc.query(
				"SELECT * FROM films_likes WHERE film_id = 1;",
				(rs, rowNum) -> rs.getInt("user_id")
		);
		List<Integer> expectedLikes = List.of(3);
		assertArrayEquals(expectedLikes.toArray(), actualLikes.toArray());
	}

	@Test
	void get_most_liked_films_test() {
		jdbc.update("INSERT INTO films_likes (film_id, user_id) VALUES (2, 2);");
		List<Integer> filmsId = likesDao.getMostLikedFilmsId(3);
		System.out.println(filmsId);
		assertArrayEquals(List.of(2, 1 ,3).toArray(), filmsId.toArray());
	}
}