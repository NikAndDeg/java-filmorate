package ru.yandex.practicum.filmorate.storage.dao.user;

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
class UserFriendsDaoImplTest {
	private final JdbcTemplate jdbc;
	private UserFriendsDao friendsDao;

	@BeforeEach
	void createUserFriendsDao() {
		friendsDao = new UserFriendsDaoImpl(jdbc);
	}

	@Test
	void save_friends_test() {
		friendsDao.save(4, Set.of(1, 2, 3));
		List<Integer> friends = jdbc.query(
				"SELECT * FROM users_friends WHERE user_id = 4;",
				(rs, rowNum) -> rs.getInt("friend_id")
		);

		assertArrayEquals(List.of(1, 2, 3).toArray(), friends.toArray());
	}

	@Test
	void get_friends_by_users_id_test() {
		Map<Integer, Set<Integer>> friends = friendsDao.get(List.of(1, 3));
		assertArrayEquals(
				List.of(2, 3).toArray(),
				friends.get(1).toArray()
		);

		assertArrayEquals(
				List.of(1).toArray(),
				friends.get(3).toArray()
		);
	}

	@Test
	void get_all_friends_test() {
		Map<Integer, Set<Integer>> friends = friendsDao.getAll();
		assertArrayEquals(
				List.of(2, 3).toArray(),
				friends.get(1).toArray()
		);

		assertArrayEquals(
				List.of(1).toArray(),
				friends.get(3).toArray()
		);

		assertArrayEquals(
				List.of(1, 3).toArray(),
				friends.get(2).toArray()
		);
	}

	@Test
	void add_friends_test() {
		friendsDao.add(4, 5);
		List<Integer> friends = jdbc.query(
				"SELECT * FROM users_friends WHERE user_id = 4;",
				(rs, rowNum) -> rs.getInt("friend_id")
		);
		assertArrayEquals(List.of(5).toArray(), friends.toArray());
	}

	@Test
	void remove_friend_test() {
		friendsDao.remove(1, 2);
		List<Integer> friends = jdbc.query(
				"SELECT * FROM users_friends WHERE user_id = 1;",
				(rs, rowNum) -> rs.getInt("friend_id")
		);

		assertArrayEquals(List.of(3).toArray(), friends.toArray());
	}

	@Test
	void get_mutual_friends_test() {
		List<Integer> friends = friendsDao.getMutualFriend(1, 2);
		assertArrayEquals(List.of(3).toArray(), friends.toArray());
	}
}