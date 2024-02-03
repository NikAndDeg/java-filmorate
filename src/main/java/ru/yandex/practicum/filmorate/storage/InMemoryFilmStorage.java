package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;
import java.util.stream.Collectors;

@Component
@Qualifier("InMemoryFilmStorage")
public class InMemoryFilmStorage implements FilmStorage {
	private final Map<Integer, Film> films = new HashMap<>();
	private int indexCounter = 0;

	@Override
	public Film save(Film film) {
		setIndex(film);
		films.put(film.getId(), film);
		return film;
	}

	@Override
	public Optional<Film> get(int index) {
		return Optional.ofNullable(films.get(index));
	}

	@Override
	public List<Film> get(List<Integer> indexes) {
		return indexes.stream()
				.map(films::get)
				.collect(Collectors.toList());
	}

	@Override
	public List<Film> getAll() {
		return new ArrayList<>(films.values());
	}

	@Override
	public Optional<Film> remove(int index) {
		return Optional.ofNullable(films.remove(index));
	}

	@Override
	public Film update(Film film) {
		films.put(film.getId(), film);
		return film;
	}

	@Override
	public boolean contains(Film film) {
		return films.containsValue(film);
	}

	private void setIndex(Film film) {
		film.setId(++indexCounter);
	}
}
