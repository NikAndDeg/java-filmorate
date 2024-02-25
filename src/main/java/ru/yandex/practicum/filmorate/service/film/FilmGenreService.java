package ru.yandex.practicum.filmorate.service.film;

import ru.yandex.practicum.filmorate.model.FilmGenre;

import java.util.Optional;
import java.util.Set;

public interface FilmGenreService {
	public Set<FilmGenre> getAllFilmGenres();
	public FilmGenre getFilmGenreById(int genreId);
}
