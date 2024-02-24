package ru.yandex.practicum.filmorate.storage.dao.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class UserFriendsDaoImpl implements UserFriendsDao {
	private final JdbcTemplate jdbc;

	@Autowired
	public UserFriendsDaoImpl(JdbcTemplate jdbc) {
		this.jdbc = jdbc;
	}

	@Override
	public void save(int userId, Set<Integer> friends) {
		String sql = "INSERT INTO users_friends (user_id, friend_id) VALUES (" + userId + ", ?);";

		if (friends == null)
			return;

		jdbc.batchUpdate(sql, friends, friends.size(), (ps, integer) -> {
			ps.setInt(1, integer);
		});
	}

	@Override
	public Set<Integer> get(int userId) {
		String sql = "SELECT friend_id FROM users_friends WHERE user_id = ?;";

		List<Integer> likes = jdbc.query(sql,
				(rs, rowNum) -> rs.getInt("friend_id"),
				userId);

		return new HashSet<>(likes);
	}

	@Override
	public Map<Integer, Set<Integer>> get(List<Integer> usersId) {
		String sql = "SELECT user_id, friend_id FROM users_friends WHERE user_id IN (%s);";

		String inSql = String.join(",", Collections.nCopies(usersId.size(), "?"));

		SqlRowSet rowSet = jdbc.queryForRowSet(String.format(sql, inSql), usersId.toArray());

		return getUsersFriends(rowSet);
	}

	@Override
	public Map<Integer, Set<Integer>> getAll() {
		String sql = "SELECT user_id, friend_id FROM users_friends;";
		return getUsersFriends(jdbc.queryForRowSet(sql));
	}

	@Override
	public void add(int userId, int friendId) {
		String sql = "INSERT INTO users_friends (user_id, friend_id) VALUES (?, ?);";
		jdbc.update(sql, userId, friendId);
	}

	@Override
	public void remove(int userId, int friendId) {
		String sql = "DELETE FROM users_friends WHERE user_id = ? AND friend_id = ?;";
		jdbc.update(sql, userId, friendId);
	}

	@Override
	public List<Integer> getMutualFriend(int userId, int otherUserId) {
		String sql = "SELECT friend_id " +
				"FROM users_friends " +
				"WHERE user_id = ? " +
				"AND friend_id IN ( " +
				"SELECT uf.friend_id " +
				"FROM USERS_FRIENDS uf " +
				"WHERE uf.user_id = ? " +
				");";

		return jdbc.query(sql, (rs, rowNum) -> rs.getInt("friend_id"), userId, otherUserId);
	}

	private Map<Integer, Set<Integer>> getUsersFriends(SqlRowSet rowSet) {
		Map<Integer, Set<Integer>> usersFriends = new HashMap<>();

		if (!rowSet.isBeforeFirst())
			return usersFriends;

		rowSet.next();

		do {
			int userId = rowSet.getInt("user_id");
			int friendId = rowSet.getInt("friend_id");
			if (!usersFriends.containsKey(friendId))
				usersFriends.put(friendId, new HashSet<>());
			usersFriends.get(friendId).add(friendId);
		} while (rowSet.next());

		return usersFriends;
	}
}
