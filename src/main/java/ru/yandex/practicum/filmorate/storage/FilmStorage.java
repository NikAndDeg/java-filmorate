package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

public interface FilmStorage extends Storage<Film> {
	@Override
	Film save(Film film);

	@Override
	Optional<Film> get(int index);

	List<Film> get(List<Integer> indexes);

	@Override
	List<Film> getAll();

	@Override
	Optional<Film> remove(int index);

	@Override
	Film update(Film film);

	@Override
	boolean contains(Film film);
}
