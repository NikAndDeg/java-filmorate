package ru.yandex.practicum.filmorate.service.film;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmGenreException;
import ru.yandex.practicum.filmorate.model.FilmGenre;
import ru.yandex.practicum.filmorate.storage.dao.film.FilmGenresDAO;

import java.util.Set;

@Service
public class FilmGenreServiceImpl implements FilmGenreService {
	private final FilmGenresDAO genresDao;

	@Autowired
	public FilmGenreServiceImpl(FilmGenresDAO genresDao) {
		this.genresDao = genresDao;
	}

	@Override
	public Set<FilmGenre> getAllFilmGenres() {
		return genresDao.getAllGenres();
	}

	@Override
	public FilmGenre getFilmGenreById(int genreId) {
		return genresDao.getGenre(genreId).orElseThrow(
				() -> new FilmGenreException("Incorrect genre id.")
		);
	}
}
