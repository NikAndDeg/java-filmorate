package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

public interface FilmStorage extends Storage<Film> {
	@Override
	Film save(Film film);

	@Override
	Optional<Film> get(int filmId);

	@Override
	List<Film> get(List<Integer> filmsId);

	@Override
	List<Film> getAll();

	@Override
	Optional<Film> remove(int filmId);

	@Override
	Film update(Film film);

	@Override
	boolean contains(Film film);

	@Override
	boolean contains(int filmId);
}
