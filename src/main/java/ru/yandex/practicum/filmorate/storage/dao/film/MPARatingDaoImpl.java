package ru.yandex.practicum.filmorate.storage.dao.film;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.MPARating;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public class MPARatingDaoImpl implements MPARatingDao {
	private final JdbcTemplate jdbcTemplate;

	@Autowired
	public MPARatingDaoImpl(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	@Override
	public Set<MPARating> getAllMPARatings() {
		String sql = "SELECT * FROM mpa_ratings;";
		List<MPARating> ratings = jdbcTemplate.query(
				sql,
				(rs, rowNum) -> MPARating.builder()
						.id(rs.getInt("mpa_rating_id"))
						.name(rs.getString("mpa_rating_name"))
						.build()
		);
		return new HashSet<>(ratings);
	}

	@Override
	public Optional<MPARating> getMPARatingByRatingId(int mpaRatingId) {
		String sql = "SELECT * FROM mpa_ratings WHERE mpa_rating_id = ?";
		List<MPARating> ratings = jdbcTemplate.query(
				sql,
				(rs, rowNum) -> MPARating.builder()
						.id(rs.getInt("mpa_rating_id"))
						.name(rs.getString("mpa_rating_name"))
						.build(),
				mpaRatingId
		);
		if (ratings.isEmpty())
			return Optional.empty();
		return Optional.of(ratings.get(0));
	}
}
