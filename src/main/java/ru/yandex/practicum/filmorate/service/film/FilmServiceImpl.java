package ru.yandex.practicum.filmorate.service.film;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import ru.yandex.practicum.filmorate.exception.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.storage.dao.film.FilmLikesDao;

import java.util.List;

@Service
@Qualifier("FilmServiceImpl")
public class FilmServiceImpl implements FilmService {
	private final FilmStorage filmStorage;
	private final UserStorage userStorage;
	private final FilmLikesDao likesDao;

	@Autowired
	public FilmServiceImpl(
			@Qualifier("FilmDaoImpl") FilmStorage filmStorage,
			@Qualifier("UserDaoImpl") UserStorage userStorage,
			FilmLikesDao likesDao) {
		this.filmStorage = filmStorage;
		this.userStorage = userStorage;
		this.likesDao = likesDao;
	}

	@Override
	public Film addFilm(Film film) {
		if (filmStorage.contains(film))
			throw new FilmAlreadyAddedException(film + " already added.");
		return filmStorage.save(film);
	}

	@Override
	public Film getFilm(int filmId) {
		return getFilmByIdOrThrowException(filmId);
	}

	@Override
	public Film updateFilm(Film film) {
		int filmId = film.getId();
		if (!filmStorage.contains(filmId))
			throw new FilmNotExistException("Film with id " + filmId + " isn't exist.");
		return filmStorage.update(film);
	}

	@Override
	public List<Film> getFilms() {
		return filmStorage.getAll();
	}

	@Override
	public Film addLike(int filmId, int userId) {
		checkUserExistInStorage(userId);
		Film film = getFilmByIdOrThrowException(filmId);
		if (film.getLikes().contains(userId))
			throw new LikeAlreadyAddedException("User already liked film.");
		likesDao.add(filmId, userId);
		film.getLikes().add(userId);
		return film;
	}

	@Override
	public Film removeLike(int filmId, int userId) {
		checkUserExistInStorage(userId);
		Film film = getFilmByIdOrThrowException(filmId);
		if (!film.getLikes().contains(userId))
			throw new LikeNotFoundException("Like not found.");
		likesDao.remove(filmId, userId);
		film.getLikes().remove(userId);
		return film;
	}

	@Override
	public List<Film> getPopularFilms(int size) {
		List<Integer> mostLikedFilmsId = likesDao.getMostLikedFilmsId(size);
		return filmStorage.get(mostLikedFilmsId);
	}

	private Film getFilmByIdOrThrowException(int filmId) {
		return filmStorage.get(filmId).orElseThrow(
				() -> new FilmNotExistException("Film with id " + filmId + " isn't exist.")
		);
	}

	private void checkUserExistInStorage(int userId) {
		userStorage.get(userId).orElseThrow(
				() -> new UserNotExistException("User with id " + userId + " isn't exist.")
		);
	}
}
