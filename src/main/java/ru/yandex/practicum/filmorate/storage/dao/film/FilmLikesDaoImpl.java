package ru.yandex.practicum.filmorate.storage.dao.film;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class FilmLikesDaoImpl implements FilmLikesDao {

	private final JdbcTemplate jdbc;

	@Autowired
	public FilmLikesDaoImpl(JdbcTemplate jdbc) {
		this.jdbc = jdbc;
	}

	@Override
	public void save(int filmId, Set<Integer> likes) {
		String sql = "INSERT INTO films_likes (film_id, user_id) VALUES (" + filmId + ", ?);";

		if (likes == null)
			return;

		jdbc.batchUpdate(sql, likes, likes.size(), (ps, integer) -> {
			ps.setInt(1, integer);
		});
	}

	@Override
	public Set<Integer> get(int filmId) {
		String sql = "SELECT user_id FROM films_likes WHERE film_id = ?;";

		List<Integer> likes = jdbc.query(sql,
				(rs, rowNum) -> rs.getInt("user_id"),
				filmId);

		return new HashSet<>(likes);
	}

	@Override
	public Map<Integer, Set<Integer>> get(List<Integer> filmsId) {
		String sql = "SELECT film_id, user_id FROM films_likes WHERE film_id IN (%s);";

		String inSql = String.join(",", Collections.nCopies(filmsId.size(), "?"));

		SqlRowSet rowSet = jdbc.queryForRowSet(String.format(sql, inSql), filmsId.toArray());

		return getFilmsLikes(rowSet);
	}

	@Override
	public Map<Integer, Set<Integer>> getAll() {
		String sql = "SELECT film_id, user_id FROM films_likes;";
		return getFilmsLikes(jdbc.queryForRowSet(sql));
	}

	@Override
	public void add(int filmId, int userId) {
		String sql = "INSERT INTO films_likes (film_id, user_id) VALUES (?, ?);";
		jdbc.update(sql, filmId, userId);
	}

	@Override
	public void remove(int filmId, int userId) {
		String sql = "DELETE FROM films_likes WHERE film_id = ? AND user_id = ?;";
		jdbc.update(sql, filmId, userId);
	}

	@Override
	public List<Integer> getMostLikedFilmsId(int size) {
		String sql =
				"SELECT f.film_id " +
						"FROM films_likes AS fl " +
						"RIGHT JOIN films AS f " +
						"ON fl.film_id = f.film_id " +
						"GROUP BY f.film_id " +
						"ORDER BY COUNT(user_id) DESC " +
						"LIMIT ?;";

		return jdbc.query(sql,
				(rs, rowNum) -> rs.getInt("film_id"),
				size);
	}

	private Map<Integer, Set<Integer>> getFilmsLikes(SqlRowSet rowSet) {
		Map<Integer, Set<Integer>> filmsLikes = new HashMap<>();

		if (!rowSet.isBeforeFirst())
			return filmsLikes;

		rowSet.next();

		do {
			int filmId = rowSet.getInt("film_id");
			int userId = rowSet.getInt("user_id");
			if (!filmsLikes.containsKey(filmId))
				filmsLikes.put(filmId, new HashSet<>());
			filmsLikes.get(filmId).add(userId);
		} while (rowSet.next());

		return filmsLikes;
	}
}
