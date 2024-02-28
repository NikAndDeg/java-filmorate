package ru.yandex.practicum.filmorate.storage.dao.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@Sql(scripts = "/test_schema.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserDaoImplTest {
	private final JdbcTemplate jdbc;
	private UserDaoImpl userDao;

	@BeforeEach
	void createUserDao() {
		userDao = new UserDaoImpl(jdbc);
	}

	@Test
	void save_user_test() {
		User savedUser = userDao.save(
				getDBSavedUsers().get(0).toBuilder()
						.email("new@mail.com")
						.login("New Login")
						.build()
		);

		User expected = getDBSavedUsers().get(0).toBuilder()
				.id(6)
				.email("new@mail.com")
				.login("New Login")
				.build();

		assertUserEquals(expected, savedUser);

		List<User> users = jdbc.query(
				"SELECT * FROM users WHERE user_id = 6;",
				(rs, rowNum) -> User.builder()
						.id(rs.getInt("user_id"))
						.email(rs.getString("email"))
						.login(rs.getString("login"))
						.name(rs.getString("user_name"))
						.birthday(rs.getDate("birthday").toLocalDate())
						.build()
		);

		assertUserEquals(expected, users.get(0));
	}

	@Test
	void get_user_by_id_test() {
		User user = userDao.get(1).get();
		assertUserEquals(getDBSavedUsers().get(0), user);
	}

	@Test
	void get_user_by_nonexistent_id() {
		assertTrue(userDao.get(999).isEmpty());
	}

	@Test
	void get_users_by_id_list_test() {
		List<User> actualUsers = userDao.get(List.of(2, 3));
		List<User> expectedUsers = List.of(
				getDBSavedUsers().get(1),
				getDBSavedUsers().get(2)
		);

		assertUserEquals(expectedUsers.get(0), actualUsers.get(0));
		assertUserEquals(expectedUsers.get(1), actualUsers.get(1));
	}

	@Test
	void get_all_users_test() {
		List<User> actualUsers =  userDao.getAll();
		List<User> expectedUsers = getDBSavedUsers();

		for (int i = 0; i < expectedUsers.size(); i++) {
			assertUserEquals(expectedUsers.get(i), actualUsers.get(i));
		}
	}

	@Test
	void remove_user_test() {
		User removedUser = userDao.remove(1).get();
		assertUserEquals(getDBSavedUsers().get(0), removedUser);

		List<User> users = jdbc.query(
				"SELECT * FROM users WHERE user_id = 1;",
				(rs, rowNum) -> User.builder()
						.id(rs.getInt("user_id"))
						.email(rs.getString("email"))
						.login(rs.getString("login"))
						.name(rs.getString("user_name"))
						.birthday(rs.getDate("birthday").toLocalDate())
						.build()
		);

		assertTrue(users.isEmpty());
	}

	@Test
	void remove_nonexistent_user_test() {
		assertTrue(userDao.remove(999).isEmpty());
	}

	@Test
	void contains_user_by_id_test() {
		assertTrue(userDao.contains(1));
		assertFalse(userDao.contains(999));
	}

	@Test
	void contains_user_by_user_object_test() {
		assertTrue(userDao.contains(getDBSavedUsers().get(2)));
		assertFalse(userDao.contains(
				getDBSavedUsers().get(1).toBuilder()
						.id(999)
						.email("qwerty@mail.com")
						.login("QWERTY")
						.build()
		));
	}

	private void assertUserEquals(User expected, User actual) {
		assertEquals(expected.getId(), actual.getId());
		assertEquals(expected.getEmail(), actual.getEmail());
		assertEquals(expected.getLogin(), actual.getLogin());
		assertEquals(expected.getName(), actual.getName());
		assertEquals(expected.getBirthday(), actual.getBirthday());
	}

	private List<User> getDBSavedUsers() {
		return List.of(
				User.builder()
						.id(1)
						.email("1mail.com")
						.login("1login")
						.name("1name")
						.birthday(LocalDate.parse("2001-01-01"))
						.build(),
				User.builder()
						.id(2)
						.email("2mail.com")
						.login("2login")
						.name("2name")
						.birthday(LocalDate.parse("2001-01-01"))
						.build(),
				User.builder()
						.id(3)
						.email("3mail.com")
						.login("3login")
						.name("3name")
						.birthday(LocalDate.parse("2001-01-01"))
						.build(),
				User.builder()
						.id(4)
						.email("4mail.com")
						.login("4login")
						.name("4name")
						.birthday(LocalDate.parse("2001-01-01"))
						.build(),
				User.builder()
						.id(5)
						.email("5mail.com")
						.login("5login")
						.name("5name")
						.birthday(LocalDate.parse("2001-01-01"))
						.build()
		);
	}
}