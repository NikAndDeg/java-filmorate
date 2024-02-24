package ru.yandex.practicum.filmorate.service.user;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import ru.yandex.practicum.filmorate.exception.UserAlreadyAddedException;
import ru.yandex.practicum.filmorate.exception.UserNotExistException;
import ru.yandex.practicum.filmorate.exception.UsersAreNotFriendsException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.storage.dao.user.UserFriendsDao;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Qualifier("UserServiceImpl")
@Slf4j
public class UserServiceImpl implements UserService {
	private final UserStorage userStorage;
	private final UserFriendsDao friendsDao;

	@Autowired
	public UserServiceImpl(@Qualifier("UserDaoImpl") UserStorage storage,
						   UserFriendsDao friendsDao) {
		this.userStorage = storage;
		this.friendsDao = friendsDao;
	}

	@Override
	public User addUser(User user) {
		setUserName(user);
		if (userStorage.contains(user))
			throw new UserAlreadyAddedException(user + " is already exist.");

		return userStorage.save(user);
	}

	@Override
	public User getUser(int userId) {
		return getUserByIdOrThrowException(userId);
	}

	@Override
	public User updateUser(User user) {
		int userId = user.getId();
		if (!userStorage.contains(userId))
			throw new UserNotExistException("User with id " + userId + " isn't exist.");
		return userStorage.update(user);
	}

	@Override
	public List<User> getUsers() {
		return userStorage.getAll();
	}

	@Override
	public User addFriend(int userId, int friendId) {
		User user = getUserByIdOrThrowException(userId);
		User friend = getUserByIdOrThrowException(friendId);
		if (user.getFriends() == null)
			user.setFriends(new HashSet<>());
		if (friend.getFriends() == null)
			friend.setFriends(new HashSet<>());
		user.getFriends().add(friendId);
		friend.getFriends().add(userId);
		friendsDao.add(userId, friendId);
		friendsDao.add(friendId, userId);
		return user;
	}

	@Override
	public User removeFriend(int userId, int friendId) {
		User user = getUserByIdOrThrowException(userId);
		User friend = getUserByIdOrThrowException(friendId);
		if (user.getFriends() == null || user.getFriends().isEmpty() || !user.getFriends().contains(friendId))
			throw new UsersAreNotFriendsException("Users with id " + userId + " and " + friendId + " are not friends.");
		user.getFriends().remove(friendId);
		friend.getFriends().remove(userId);
		friendsDao.remove(userId, friendId);
		friendsDao.remove(friendId, userId);
		return user;
	}

	@Override
	public List<User> getFriends(int userId) {
		if (!userStorage.contains(userId))
			throw new UserNotExistException("User with id " + userId + " isn't exist.");
		Set<Integer> friends = friendsDao.get(userId);
		return userStorage.get(new ArrayList<>(friends));
	}

	@Override
	public List<User> getMutualFriends(int userId, int otherUserId) {
		if (!userStorage.contains(userId))
			throw new UserNotExistException("User with id " + userId + " isn't exist.");
		if (!userStorage.contains(otherUserId))
			throw new UserNotExistException("User with id " + userId + " isn't exist.");
		List<Integer> mutualFriends = friendsDao.getMutualFriend(userId, otherUserId);
		return userStorage.get(new ArrayList<>(mutualFriends));
	}

	private void setUserName(User user) {
		String name = user.getName();
		if (name == null || name.isBlank() || name.isEmpty()) {
			user.setName(user.getLogin());
			log.debug("Set user's name on [{}]", user.getName());
		}
	}

	private User getUserByIdOrThrowException(int userId) {
		return userStorage.get(userId).orElseThrow(
				() -> new UserNotExistException("User with id " + userId + " isn't exist.")
		);
	}
}
