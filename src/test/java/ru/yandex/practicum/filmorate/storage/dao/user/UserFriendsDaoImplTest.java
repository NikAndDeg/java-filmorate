package ru.yandex.practicum.filmorate.storage.dao.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Sql(scripts = "/test_schema.sql",
		executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class UserFriendsDaoImplTest {
	@Autowired
	private UserFriendsDaoImpl friendsDao;

	@Test
	void add_and_get_friend() {
		friendsDao.add(1, 2);
		assertArrayEquals(List.of(2).toArray(), friendsDao.get(1).toArray());
	}

	@Test
	void get_mutual_friends() {
		friendsDao.add(1, 2);
		friendsDao.add(1, 3);
		friendsDao.add(1, 4);

		friendsDao.add(3, 2);
		friendsDao.add(3, 3);
		friendsDao.add(3, 1);

		assertArrayEquals(List.of(2, 3).toArray(), friendsDao.getMutualFriend(1, 3).toArray());
		assertArrayEquals(List.of(2, 3).toArray(), friendsDao.getMutualFriend(3, 1).toArray());
	}

}