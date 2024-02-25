package ru.yandex.practicum.filmorate.storage.dao.film;

import ru.yandex.practicum.filmorate.model.FilmGenre;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface FilmGenresDAO {
	void save(Set<FilmGenre> genres, int filmId);
	Set<FilmGenre> getByFilmId(int filmId);
	Map<Integer, Set<FilmGenre>> getByFilmsId(List<Integer> filmsId);
	Map<Integer, Set<FilmGenre>> getAll();
	Set<FilmGenre> get(Set<Integer> genresId);
	Set<FilmGenre> getAllGenres();
	Optional<FilmGenre> getGenre(int genreId);
	void update(int filmId, Set<FilmGenre> genres);
}
