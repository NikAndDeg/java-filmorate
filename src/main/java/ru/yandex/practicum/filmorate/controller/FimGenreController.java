package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.FilmGenre;
import ru.yandex.practicum.filmorate.service.film.FilmGenreService;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@RestController
@RequestMapping("/genres")
public class FimGenreController {
	private final FilmGenreService genreService;

	@Autowired
	public FimGenreController(FilmGenreService genreService) {
		this.genreService = genreService;
	}

	@GetMapping()
	public List<FilmGenre> getAllFilmGenres() {
		List<FilmGenre> genres = new ArrayList<>(genreService.getAllFilmGenres());
		genres.sort(Comparator.comparingInt(FilmGenre::getId));
		return genres;
	}

	@GetMapping("/{id}")
	public FilmGenre getFilmGenreById(@PathVariable int id) {
		return genreService.getFilmGenreById(id);
	}
}
