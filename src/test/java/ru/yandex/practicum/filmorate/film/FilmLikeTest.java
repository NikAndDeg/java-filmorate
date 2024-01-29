package ru.yandex.practicum.filmorate.film;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Set;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class FilmLikeTest {

	@Autowired
	FilmController filmController;

	@Autowired
	UserController userController;

	private final Film defaultFilm = Film.builder()
			.name("First Film")
			.releaseDate(LocalDate.of(2000, 12, 12))
			.duration(30)
			.build();

	private final User defaultUser = User.builder()
			.email("firstUser@mail.com")
			.login("First User")
			.birthday(LocalDate.of(2000, 12, 12))
			.build();

	@Test
	@Order(1)
	void add_like() {
		userController.postUser(defaultUser);
		filmController.postFilm(defaultFilm);

		filmController.addLike(1, 1);

		assertEquals(
				Set.of(1),
				filmController.getFilm(1).getLikes()
		);
	}

	@Test
	@Order(2)
	void delete_like() {
		filmController.deleteLike(1, 1);

		assertEquals(
				Set.of(),
				filmController.getFilm(1).getLikes()
		);
	}
}