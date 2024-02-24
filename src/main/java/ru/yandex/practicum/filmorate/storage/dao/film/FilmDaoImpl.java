package ru.yandex.practicum.filmorate.storage.dao.film;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;

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

	@Autowired
	public FilmDaoImpl(JdbcTemplate jdbc, FilmLikesDao likesDao) {
		this.jdbc = jdbc;
		this.likesDao = likesDao;
	}

	@Override
	public Film save(Film film) {
		String sql =
				"INSERT INTO films (film_name, description, release_date, duration) VALUES (?, ?, ?, ?);";

		KeyHolder keyHolder = new GeneratedKeyHolder();

		jdbc.update(connection -> {
			PreparedStatement ps = connection
					.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			ps.setString(1, film.getName());
			ps.setString(2, film.getDescription());
			ps.setString(3, film.getReleaseDate().toString());
			ps.setInt(4, film.getDuration());
			return ps;
		}, keyHolder);

		film.setId((int) keyHolder.getKey());

		likesDao.save(film.getId(), film.getLikes());

		return film;
	}

	@Override
	public Optional<Film> get(int filmId) {
		String sql = "SELECT * FROM films WHERE film_id = ?;";

		List<Film> films = jdbc.query(sql, this::filmMapRow, filmId);

		if (films.isEmpty())
			return Optional.empty();

		Film film = films.get(0);

		Set<Integer> likes = likesDao.get(filmId);

		film.setLikes(likes);

		return Optional.of(film);
	}

	@Override
	public List<Film> get(List<Integer> filmsId) {
		String sql = "SELECT * FROM films WHERE film_id IN (%s);";

		String inSql = String.join(",", Collections.nCopies(filmsId.size(), "?"));

		List<Film> films = jdbc.query(String.format(sql, inSql), this::filmMapRow, filmsId.toArray());

		Map<Integer, Set<Integer>> filmsLikes = likesDao.get(filmsId);

		return addLikesToFilms(films, filmsLikes);
	}

	@Override
	public List<Film> getAll() {
		String sql = "SELECT * FROM films;";

		List<Film> films = jdbc.query(sql, this::filmMapRow);

		Map<Integer, Set<Integer>> filmsLikes = likesDao.getAll();

		return addLikesToFilms(films, filmsLikes);
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
		String sql =
				"UPDATE films SET film_name = ?, description = ?, release_date = ?, duration =? WHERE film_id = ?;";

		jdbc.update(sql, ps -> {
			ps.setString(1, film.getName());
			ps.setString(2, film.getDescription());
			ps.setString(3, film.getReleaseDate().toString());
			ps.setInt(4, film.getDuration());
			ps.setInt(5, film.getId());
		});

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
				.build();
	}

	private List<Film> addLikesToFilms(List<Film> films, Map<Integer, Set<Integer>> filmsLikes) {
		for (Film film : films) {
			int filmId = film.getId();
			Set<Integer> likes = filmsLikes.get(filmId);
			if (likes != null)
				film.setLikes(likes);
			else
				film.setLikes(new HashSet<>());
		}

		return films;
	}
}
