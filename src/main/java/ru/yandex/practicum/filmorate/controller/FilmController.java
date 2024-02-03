package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.film.FilmService;

import javax.validation.Valid;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
	private final FilmService filmService;

	@Autowired
	public FilmController(@Qualifier("FilmServiceImpl") FilmService filmService) {
		this.filmService = filmService;
	}

	@PostMapping
	public Film postFilm(@Valid @RequestBody Film film) {
		return filmService.addFilm(film);
	}

	@PutMapping
	public Film putFilm(@Valid @RequestBody Film film) {
		return filmService.updateFilm(film);
	}

	@GetMapping
	public List<Film> getFilms() {
		return filmService.getFilms();
	}

	@GetMapping("/{id}")
	public Film getFilm(@PathVariable("id") int filmId) {
		return filmService.getFilm(filmId);
	}

	@PutMapping("/{id}/like/{userId}")
	public Film addLike(@PathVariable("id") int filmId, @PathVariable int userId) {
		return filmService.addLike(filmId, userId);
	}

	@DeleteMapping("/{id}/like/{userId}")
	public Film deleteLike(@PathVariable("id") int filmId, @PathVariable int userId) {
		return filmService.removeLike(filmId, userId);
	}

	@GetMapping("/popular")
	public List<Film> getPopularFilms(@RequestParam(name = "count", defaultValue = "10") String size) {
		return filmService.getPopularFilms(Integer.parseInt(size));
	}
}
