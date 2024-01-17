package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import ru.yandex.practicum.filmorate.exception.FilmAlreadyAddedException;
import ru.yandex.practicum.filmorate.exception.FilmNotExistException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
	private final Map<Integer, Film> films = new HashMap<>();
	private int idCounter = 0;

	@PostMapping
	public Film postFilm(@Valid @RequestBody Film film) {
		log.info("Post [" + film.toString() + "].");
		filmExistCheck(film);
		addFilm(film);
		log.info("Film posted successfully. Films size [" + films.size() + "].");
		return film;
	}

	@PutMapping
	public Film putFilm(@Valid @RequestBody Film film) {
		log.info("Put [" + film.toString() + "].");
		int filmId = film.getId();
		if (!films.containsKey(filmId))
			throw new FilmNotExistException("Film with id " + filmId + " isn't exist.");
		films.put(filmId, film);
		log.info("Film put successfully.");
		return film;
	}

	@GetMapping
	public List<Film> getFilms() {
		return new ArrayList<>(films.values());
	}

	private void filmExistCheck(Film film) {
		if (films.containsValue(film)) {
			throw new FilmAlreadyAddedException("Film with name " + film.getName()
					+ " and same description is already added.");
		}
	}

	private void addFilm(Film film) {
		film.setId(++idCounter);
		films.put(idCounter, film);
	}
}
