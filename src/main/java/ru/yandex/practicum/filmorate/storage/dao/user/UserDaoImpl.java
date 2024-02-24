package ru.yandex.practicum.filmorate.storage.dao.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

@Repository
@Qualifier("UserDaoImpl")
public class UserDaoImpl implements UserDao {
	private final JdbcTemplate jdbc;
	private final UserFriendsDao friendsDao;

	@Autowired
	public UserDaoImpl(JdbcTemplate jdbc, UserFriendsDao friendsDao) {
		this.jdbc = jdbc;
		this.friendsDao = friendsDao;
	}

	@Override
	public User save(User user) {
		String sql = "INSERT INTO users (email, login, user_name, birthday) VALUES (?, ?, ?, ?);";

		KeyHolder keyHolder = new GeneratedKeyHolder();

		jdbc.update(connection -> {
			PreparedStatement ps = connection
					.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			ps.setString(1, user.getEmail());
			ps.setString(2, user.getLogin());
			ps.setString(3, user.getName());
			ps.setString(4, user.getBirthday().toString());
			return ps;
		}, keyHolder);

		user.setId((int) keyHolder.getKey());

		return user;
	}

	@Override
	public Optional<User> get(int userId) {
		String sql = "SELECT * FROM users WHERE user_id = ?;";

		List<User> users = jdbc.query(sql, this::userMapRow, userId);

		if (users.isEmpty())
			return Optional.empty();

		User user = users.get(0);
		Set<Integer> friends = friendsDao.get(userId);
		user.setFriends(friends);

		return Optional.of(user);
	}

	@Override
	public List<User> get(List<Integer> usersId) {
		String sql = "SELECT * FROM users WHERE user_id IN (%s);";

		String inSql = String.join(",", Collections.nCopies(usersId.size(), "?"));

		List<User> users = jdbc.query(String.format(sql, inSql), this::userMapRow, usersId.toArray());

		Map<Integer, Set<Integer>> usersFriends = friendsDao.get(usersId);

		return addFriendsToUser(users, usersFriends);
	}

	@Override
	public List<User> getAll() {
		String sql = "SELECT * FROM users;";

		List<User> users = jdbc.query(sql, this::userMapRow);

		Map<Integer, Set<Integer>> usersFriends = friendsDao.getAll();

		return addFriendsToUser(users, usersFriends);
	}

	@Override
	public Optional<User> remove(int userId) {
		String sql = "DELETE FROM users HERE user_id = ?;";

		Optional<User> user = get(userId);

		if (user.isPresent())
			jdbc.update(sql, userId);

		return user;
	}

	@Override
	public User update(User user) {
		String sql = "UPDATE users SET email = ?, login = ?, user_name = ?, birthday = ? WHERE user_id = ?;";

		jdbc.update(sql, ps -> {
			ps.setString(1, user.getEmail());
			ps.setString(2, user.getLogin());
			ps.setString(3, user.getName());
			ps.setString(4, user.getBirthday().toString());
			ps.setInt(5, user.getId());
		});

		return get(user.getId()).get();
	}

	@Override
	public boolean contains(User user) {
		String sql = "SELECT user_id FROM users WHERE email = ? OR login = ?;";
		List<Integer> usersId = jdbc.query(sql,
				(rs, rowNum) -> rs.getInt("user_id"),
				user.getEmail(),
				user.getLogin());
		return !usersId.isEmpty();
	}

	@Override
	public boolean contains(int userId) {
		String sql = "SELECT user_id FROM users WHERE user_id = ?;";
		List<Integer> usersId = jdbc.query(sql,
				(rs, rowNum) -> rs.getInt("user_id"),
				userId);
		return !usersId.isEmpty();
	}

	private User userMapRow(ResultSet rs, int rowNum) throws SQLException {
		return User.builder()
				.id(rs.getInt("user_id"))
				.email(rs.getString("email"))
				.login(rs.getString("login"))
				.name(rs.getString("user_name"))
				.birthday(rs.getDate("birthday").toLocalDate())
				.build();
	}

	private List<User> addFriendsToUser(List<User> users, Map<Integer, Set<Integer>> usersFriends) {
		for (User user : users) {
			int userId = user.getId();
			Set<Integer> friends = usersFriends.get(userId);
			if (friends != null)
				user.setFriends(friends);
			else
				user.setFriends(new HashSet<>());
		}

		return users;
	}
}
