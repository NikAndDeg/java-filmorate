package ru.yandex.practicum.filmorate.service.film;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import ru.yandex.practicum.filmorate.exception.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmGenre;
import ru.yandex.practicum.filmorate.model.MPARating;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.storage.dao.film.FilmGenresDAO;
import ru.yandex.practicum.filmorate.storage.dao.film.FilmLikesDao;
import ru.yandex.practicum.filmorate.storage.dao.film.MPARatingDao;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Qualifier("FilmServiceImpl")
public class FilmServiceImpl implements FilmService {
	private final FilmStorage filmStorage;
	private final UserStorage userStorage;
	private final FilmLikesDao likesDao;
	private final FilmGenresDAO genresDao;
	private final MPARatingDao ratingDao;

	@Autowired
	public FilmServiceImpl(
			@Qualifier("FilmDaoImpl") FilmStorage filmStorage,
			@Qualifier("UserDaoImpl") UserStorage userStorage,
			FilmLikesDao likesDao,
			FilmGenresDAO genresDao,
			MPARatingDao ratingDao
	) {
		this.filmStorage = filmStorage;
		this.userStorage = userStorage;
		this.likesDao = likesDao;
		this.genresDao = genresDao;
		this.ratingDao = ratingDao;
	}

	@Override
	public Film addFilm(Film film) {
		if (filmStorage.contains(film))
			throw new FilmAlreadyAddedException(film + " already added.");
		setGenresNamesToFilm(film);
		setMPARatingToFilm(film);
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
		setGenresNamesToFilm(film);
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

	private void setGenresNamesToFilm(Film film) {
		Set<FilmGenre> filmGenres = film.getGenres();
		if (filmGenres == null || filmGenres.isEmpty())
			return;
		Set<FilmGenre> genresFromDb = genresDao.get(
				filmGenres.stream()
						.map(FilmGenre::getId)
						.collect(Collectors.toSet())
		);
		if (filmGenres.size() != genresFromDb.size())
			throw new FilmGenreException("Incorrect genres.");
		film.setGenres(genresFromDb);
	}

	private void setMPARatingToFilm(Film film) {
		if (film.getMpa() == null)
			return;
		int mpaRatingId = film.getMpa().getId();
		MPARating rating = ratingDao.getMPARatingByRatingId(mpaRatingId).orElseThrow(
				() -> new MPARatingException("")
		);
		film.setMpa(rating);
	}
}
