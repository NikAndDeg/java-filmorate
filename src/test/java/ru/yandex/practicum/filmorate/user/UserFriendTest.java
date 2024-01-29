package ru.yandex.practicum.filmorate.user;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Set;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserFriendTest {

	@Autowired
	UserController controller;

	private final User defaultUser = User.builder()
			.email("firstUser@mail.com")
			.login("First User")
			.birthday(LocalDate.of(2000, 12, 12))
			.build();

	@Test
	@Order(2)
	void add_friend() {
		controller.postUser(defaultUser);
		controller.postUser(
				defaultUser.toBuilder()
						.email("secondUser@mail.com")
						.login("Second User")
						.build()
		);

		controller.addFriend(1, 2);

		assertEquals(
				Set.of(2),
				controller.getUser(1).getFriends()
		);
		assertEquals(
				Set.of(1),
				controller.getUser(2).getFriends()
		);
	}

	@Test
	@Order(3)
	void remove_friend() {
		controller.removeFriend(2, 1);

		assertEquals(
				Set.of(),
				controller.getUser(1).getFriends()
		);
		assertEquals(
				Set.of(),
				controller.getUser(2).getFriends()
		);
	}
}