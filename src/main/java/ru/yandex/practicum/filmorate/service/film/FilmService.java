package ru.yandex.practicum.filmorate.service.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmService {
	Film addFilm(Film film);

	Film getFilm(int filmId);

	Film updateFilm(Film film);

	List<Film> getFilms();

	Film addLike(int filmId, int userId);

	Film removeLike(int filmId, int userId);

	List<Film> getPopularFilms(int size);
}
