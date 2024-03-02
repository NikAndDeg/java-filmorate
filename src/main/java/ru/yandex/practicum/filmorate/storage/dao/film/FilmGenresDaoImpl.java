package ru.yandex.practicum.filmorate.storage.dao.film;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.FilmGenre;

import java.util.*;

@Repository
public class FilmGenresDaoImpl implements FilmGenresDAO {
	private final JdbcTemplate jdbc;

	@Autowired
	public FilmGenresDaoImpl(JdbcTemplate jdbc) {
		this.jdbc = jdbc;
	}

	@Override
	public void save(Set<FilmGenre> genres, int filmId) {
		String sql = "INSERT INTO films_genres (film_id, genre_id) VALUES (" + filmId + ", ?);";
		if (genres == null || genres.isEmpty())
			return;
		jdbc.batchUpdate(
				sql,
				genres,
				genres.size(),
				(ps, filmGenre) -> ps.setInt(1, filmGenre.getId())
		);
	}

	@Override
	public Set<FilmGenre> getByFilmId(int filmId) {
		String sql = "SELECT fg.genre_id, g.genre_name " +
				"FROM films_genres fg " +
				"LEFT JOIN genres g " +
				"ON fg.genre_id = g.genre_id " +
				"WHERE fg.film_id = ?;";
		List<FilmGenre> genres = jdbc.query(
				sql,
				(rs, rowNum) -> FilmGenre.builder()
						.id(rs.getInt("genre_id"))
						.name(rs.getString("genre_name"))
						.build(),
				filmId
		);
		return new HashSet<>(genres);
	}

	@Override
	public Map<Integer, Set<FilmGenre>> getByFilmsId(List<Integer> filmsId) {
		String sql = "SELECT fg.film_id, fg.genre_id, g.genre_name " +
				"FROM films_genres fg " +
				"LEFT JOIN genres g " +
				"ON fg.genre_id = g.genre_id " +
				"WHERE fg.film_id IN (%s);";
		String inSql = String.join(",", Collections.nCopies(filmsId.size(), "?"));
		SqlRowSet rowSet = jdbc.queryForRowSet(String.format(sql, inSql), filmsId.toArray());

		return getFilmsGenres(rowSet);
	}

	@Override
	public Map<Integer, Set<FilmGenre>> getAll() {
		String sql = "SELECT fg.film_id, fg.genre_id, g.genre_name " +
				"FROM films_genres fg " +
				"LEFT JOIN genres g " +
				"ON fg.genre_id = g.genre_id;";
		SqlRowSet rowSet = jdbc.queryForRowSet(sql);

		return getFilmsGenres(rowSet);
	}

	private Map<Integer, Set<FilmGenre>> getFilmsGenres(SqlRowSet rowSet) {
		Map<Integer, Set<FilmGenre>> filmsGenre = new HashMap<>();
		if (!rowSet.isBeforeFirst())
			return filmsGenre;
		rowSet.next();
		do {
			int filmId = rowSet.getInt("film_id");
			int genreId = rowSet.getInt("genre_id");
			String genreName = rowSet.getString("genre_name");
			if (!filmsGenre.containsKey(filmId))
				filmsGenre.put(filmId, new HashSet<>());
			filmsGenre.get(filmId).add(
					FilmGenre.builder()
							.id(genreId)
							.name(genreName)
							.build()
			);
		} while (rowSet.next());
		return filmsGenre;
	}

	@Override
	public Set<FilmGenre> get(Set<Integer> genresId) {
		String sql = "SELECT * FROM genres WHERE genre_id IN (%s);";
		String inSql = String.join(",", Collections.nCopies(genresId.size(), "?"));
		List<FilmGenre> genres = jdbc.query(
				String.format(sql, inSql),
				(rs, rowNum) -> FilmGenre.builder()
						.id(rs.getInt("genre_id"))
						.name(rs.getString("genre_name"))
						.build(),
				genresId.toArray()
		);
		return new HashSet<>(genres);
	}

	@Override
	public Set<FilmGenre> getAllGenres() {
		String sql = "SELECT * FROM genres;";
		List<FilmGenre> genres = jdbc.query(
				sql,
				(rs, rowNum) -> FilmGenre.builder()
						.id(rs.getInt("genre_id"))
						.name(rs.getString("genre_name"))
						.build()
		);
		return new HashSet<>(genres);
	}

	@Override
	public Optional<FilmGenre> getGenre(int genreId) {
		String sql = "SELECT * FROM genres WHERE genre_id =?;";
		List<FilmGenre> genres = jdbc.query(
				sql,
				(rs, rowNum) -> FilmGenre.builder()
						.id(rs.getInt("genre_id"))
						.name(rs.getString("genre_name"))
						.build(),
				genreId
		);
		if (genres.isEmpty())
			return Optional.empty();
		return Optional.of(genres.get(0));
	}

	@Override
	public void update(int filmId, Set<FilmGenre> genres) {
		String sql = "DELETE FROM films_genres WHERE film_id = ?;";
		jdbc.update(sql, filmId);
		save(genres, filmId);
	}
}
