package ru.yandex.practicum.filmorate.storage.dao.film;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.model.MPARating;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@Sql(scripts = "/test_schema.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class MPARatingDaoImplTest {
	private final JdbcTemplate jdbc;
	private MPARatingDaoImpl mpaRatingDao;
	private List<MPARating> allRatings = List.of(
			MPARating.builder()
					.id(1)
					.name("G")
					.build(),
			MPARating.builder()
					.id(2)
					.name("PG")
					.build(),
			MPARating.builder()
					.id(3)
					.name("PG-13")
					.build(),
			MPARating.builder()
					.id(4)
					.name("R")
					.build(),
			MPARating.builder()
					.id(5)
					.name("NC-17")
					.build()
	);

	@BeforeEach
	void createMPARatingDao() {
		mpaRatingDao = new MPARatingDaoImpl(jdbc);
	}

	@Test
	void get_all_ratings_test() {
		List<MPARating> ratings = new ArrayList<>(mpaRatingDao.getAllMPARatings());
		ratings.sort(Comparator.comparingInt(MPARating::getId));
		assertArrayEquals(allRatings.toArray(), ratings.toArray());
	}

	@Test
	void get_rating_by_id() {
		assertEquals(allRatings.get(1), mpaRatingDao.getMPARatingByRatingId(2).get());
	}

	@Test
	void get_rating_by_nonexistent_id() {
		assertTrue(mpaRatingDao.getMPARatingByRatingId(999).isEmpty());
	}
}