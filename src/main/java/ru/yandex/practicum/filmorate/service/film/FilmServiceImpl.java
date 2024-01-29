package ru.yandex.practicum.filmorate.service.film;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import ru.yandex.practicum.filmorate.exception.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Qualifier("FilmServiceImpl")
public class FilmServiceImpl implements FilmService {
	FilmStorage filmStorage;
	UserStorage userStorage;

	@Autowired
	public FilmServiceImpl(
			@Qualifier("InMemoryFilmStorage") FilmStorage filmStorage,
			@Qualifier("InMemoryUserStorage") UserStorage userStorage) {
		this.filmStorage = filmStorage;
		this.userStorage = userStorage;
	}

	@Override
	public Film addFilm(Film film) {
		if (filmStorage.contains(film))
			throw new FilmAlreadyAddedException(film + " already added.");
		return filmStorage.save(film);
	}

	@Override
	public Film getFilm(int filmId) {
		return filmStorage.get(filmId).orElseThrow(
				() -> new FilmNotExistException("Film with id " + filmId + " isn't exist.")
		);
	}

	@Override
	public Film updateFilm(Film film) {
		int filmId = film.getId();
		filmStorage.get(filmId).orElseThrow(
				() -> new FilmNotExistException("Film with id " + filmId + " isn't exist.")
		);
		return filmStorage.update(film);
	}

	@Override
	public List<Film> getFilms() {
		return filmStorage.getAll();
	}

	@Override
	public Film addLike(int filmId, int userId) {
		userStorage.get(userId).orElseThrow(
				() -> new UserNotExistException("User with id " + userId + " isn't exist.")
		);
		Film film = filmStorage.get(filmId).orElseThrow(
				() -> new FilmNotExistException("Film with id " + filmId + " isn't exist.")
		);
		if (film.getLikes() == null)
			film.setLikes(new HashSet<>());
		else if (film.getLikes().contains(userId))
			throw new LikeAlreadyAddedException("User already liked film.");
		film.getLikes().add(userId);
		return film;
	}

	@Override
	public Film removeLike(int filmId, int userId) {
		userStorage.get(userId).orElseThrow(
				() -> new UserNotExistException("User with id " + userId + " isn't exist.")
		);
		Film film = filmStorage.get(filmId).orElseThrow(
				() -> new FilmNotExistException("Film with id " + filmId + " isn't exist.")
		);
		if (film.getLikes() == null)
			film.setLikes(new HashSet<>());
		else if (!film.getLikes().contains(userId))
			throw new LikeNotFoundException("Like not found.");
		film.getLikes().remove(userId);
		return film;
	}

	@Override
	public List<Film> getPopularFilms(int size) {
		List<Film> films = filmStorage.getAll();
		if (films == null || films.isEmpty())
			return new ArrayList<>();
		return filmStorage.getAll().stream()
				.sorted((f1, f2) -> {
					if (f1.getLikes() == null)
						return 1;
					if (f2.getLikes() == null)
						return -1;
					return f2.getLikes().size() - f1.getLikes().size();
						}
				)
				.limit(size)
				.collect(Collectors.toList());
	}
}
