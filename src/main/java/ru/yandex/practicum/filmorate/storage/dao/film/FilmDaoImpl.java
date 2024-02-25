package ru.yandex.practicum.filmorate.storage.dao.film;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmGenre;
import ru.yandex.practicum.filmorate.model.MPARating;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

@Repository
@Qualifier("FilmDaoImpl")
public class FilmDaoImpl implements FilmDao {
	private final JdbcTemplate jdbc;
	private final FilmLikesDao likesDao;
	private final FilmGenresDAO genresDao;
	private final MPARatingDao ratingDao;

	@Autowired
	public FilmDaoImpl(
			JdbcTemplate jdbc,
			FilmLikesDao likesDao,
			FilmGenresDAO genresDao,
			MPARatingDao ratingDao
	) {
		this.jdbc = jdbc;
		this.likesDao = likesDao;
		this.genresDao = genresDao;
		this.ratingDao = ratingDao;
	}

	@Override
	public Film save(Film film) {
		String sql =
				"INSERT INTO films (film_name, description, release_date, duration, mpa_rating_id) " +
						"VALUES (?, ?, ?, ?, ?);";
		saveFilmAndSetId(sql, film);
		saveFilmLikes(film);
		saveFilmGenres(film);

		return film;
	}

	@Override
	public Optional<Film> get(int filmId) {
		String sql = "SELECT " +
				"f.FILM_ID, " +
				"f.FILM_NAME, " +
				"f.DESCRIPTION, " +
				"f.RELEASE_DATE, " +
				"f.DURATION, " +
				"f.MPA_RATING_ID, " +
				"mr.MPA_RATING_NAME " +
				"FROM FILMS f " +
				"LEFT JOIN MPA_RATINGS mr " +
				"ON f.MPA_RATING_ID = mr.MPA_RATING_ID " +
				"WHERE f.FILM_ID = ?;";
		List<Film> films = jdbc.query(sql, this::filmMapRow, filmId);
		if (films.isEmpty())
			return Optional.empty();
		Film film = films.get(0);
		addLikesToFilm(film);
		addGenresToFilm(film);
		return Optional.of(film);
	}

	@Override
	public List<Film> get(List<Integer> filmsId) {
		String sql = "SELECT " +
				"f.FILM_ID, " +
				"f.FILM_NAME, " +
				"f.DESCRIPTION, " +
				"f.RELEASE_DATE, " +
				"f.DURATION, " +
				"f.MPA_RATING_ID, " +
				"mr.MPA_RATING_NAME " +
				"FROM FILMS f " +
				"LEFT JOIN MPA_RATINGS mr " +
				"ON f.MPA_RATING_ID = mr.MPA_RATING_ID " +
				"WHERE FILM_ID IN (%s);";
		String inSql = String.join(",", Collections.nCopies(filmsId.size(), "?"));
		List<Film> films = jdbc.query(String.format(sql, inSql), this::filmMapRow, filmsId.toArray());
		addLikesToFilms(films, likesDao.get(filmsId));
		addGenresToFilms(films, genresDao.getByFilmsId(filmsId));
		return films;
	}

	@Override
	public List<Film> getAll() {
		String sql = "SELECT " +
				"f.FILM_ID, " +
				"f.FILM_NAME, " +
				"f.DESCRIPTION, " +
				"f.RELEASE_DATE, " +
				"f.DURATION, " +
				"f.MPA_RATING_ID, " +
				"mr.MPA_RATING_NAME " +
				"FROM FILMS f " +
				"LEFT JOIN MPA_RATINGS mr " +
				"ON f.MPA_RATING_ID = mr.MPA_RATING_ID;";
		List<Film> films = jdbc.query(sql, this::filmMapRow);
		addLikesToFilms(films, likesDao.getAll());
		addGenresToFilms(films, genresDao.getAll());
		return films;
	}

	@Override
	public Optional<Film> remove(int filmId) {
		String sql = "DELETE FROM films WHERE film_id = ?;";
		Optional<Film> film = get(filmId);
		if (film.isPresent())
			jdbc.update(sql, filmId);
		return film;
	}

	@Override
	public Film update(Film film) {
		String sql = "UPDATE films " +
				"SET film_name = ?, " +
				"description = ?, " +
				"release_date = ?, " +
				"duration =?, " +
				"mpa_rating_id =? " +
				"WHERE film_id = ?;";
		jdbc.update(sql, ps -> {
			ps.setString(1, film.getName());
			ps.setString(2, film.getDescription());
			ps.setString(3, film.getReleaseDate().toString());
			ps.setInt(4, film.getDuration());
			ps.setInt(5, film.getMpa().getId());
			ps.setInt(6, film.getId());
		});
		genresDao.update(film.getId(), film.getGenres());
		return get(film.getId()).get();
	}

	@Override
	public boolean contains(Film film) {
		String sql = "SELECT film_id FROM films WHERE film_name = ? AND description = ?;";
		List<Integer> id = jdbc.query(sql,
				(rs, rowNum) -> rs.getInt("film_id"),
				film.getName(),
				film.getDescription());
		return !id.isEmpty();
	}

	@Override
	public boolean contains(int filmId) {
		String sql = "SELECT film_id FROM films WHERE film_id = ?;";
		List<Integer> id = jdbc.query(sql,
				(rs, rowNum) -> rs.getInt("film_id"),
				filmId);
		return !id.isEmpty();
	}

	private Film filmMapRow(ResultSet rs, int rowNum) throws SQLException {
		int filmId = rs.getInt("film_id");
		return Film.builder()
				.id(filmId)
				.name(rs.getString("film_name"))
				.description(rs.getString("description"))
				.releaseDate(rs.getDate("release_date").toLocalDate())
				.duration(rs.getInt("duration"))
				.mpa(
						MPARating.builder()
								.id(rs.getInt("MPA_RATING_ID"))
								.name(rs.getString("MPA_RATING_NAME"))
								.build()
				)
				.build();
	}

	private void addLikesToFilms(List<Film> films, Map<Integer, Set<Integer>> filmsLikes) {
		for (Film film : films) {
			int filmId = film.getId();
			Set<Integer> likes = filmsLikes.get(filmId);
			if (likes != null)
				film.setLikes(likes);
			else
				film.setLikes(new HashSet<>());
		}
	}


	private void saveFilmAndSetId(String sql, Film film) {
		KeyHolder keyHolder = new GeneratedKeyHolder();
		jdbc.update(connection -> {
			PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			ps.setString(1, film.getName());
			ps.setString(2, film.getDescription());
			ps.setString(3, film.getReleaseDate().toString());
			ps.setInt(4, film.getDuration());
			ps.setInt(5, film.getMpa().getId());
			return ps;
		}, keyHolder);
		film.setId((int) keyHolder.getKey());
	}

	private void saveFilmLikes(Film film) {
		likesDao.save(film.getId(), film.getLikes());
	}

	private void saveFilmGenres(Film film) {
		genresDao.save(film.getGenres(), film.getId());
	}

	private void addLikesToFilm(Film film) {
		Set<Integer> likes = likesDao.get(film.getId());
		film.setLikes(likes);
	}

	private void addGenresToFilm(Film film) {
		Set<FilmGenre> genres = genresDao.getByFilmId(film.getId());
		film.setGenres(genres);
	}

	private void addGenresToFilms(List<Film> films, Map<Integer, Set<FilmGenre>> filmsGenres) {
		for (Film film : films) {
			int filmId = film.getId();
			Set<FilmGenre> genres = filmsGenres.get(filmId);
			if (genres != null)
				film.setGenres(genres);
			else
				film.setGenres(new HashSet<>());
		}
	}
}
