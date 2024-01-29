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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Qualifier("UserServiceImpl")
@Slf4j
public class UserServiceImpl implements UserService {
	UserStorage userStorage;

	@Autowired
	public UserServiceImpl(@Qualifier("InMemoryUserStorage") UserStorage storage) {
		this.userStorage = storage;
	}

	@Override
	public User addUser(User user) {
		setUserName(user);
		if (userStorage.contains(user))
			throw new UserAlreadyAddedException(user + " is already exist.");
		if (userStorage.containsEmail(user))
			throw new UserAlreadyAddedException("User with email " + user.getEmail() + " is already exist.");
		if (userStorage.containsName(user))
			throw new UserAlreadyAddedException("User with name " + user.getName() + " is already exist.");
		if (userStorage.containsLogin(user))
			throw new UserAlreadyAddedException("User with login " + user.getLogin() + " is already exist");
		return userStorage.save(user);
	}

	@Override
	public User getUser(int userId) {
		return userStorage.get(userId).orElseThrow(
				() -> new UserNotExistException("User with id " + userId + " isn't exist.")
		);
	}

	@Override
	public User updateUser(User user) {
		int userId = user.getId();
		userStorage.get(userId).orElseThrow(
				() -> new UserNotExistException("User with id " + userId + " isn't exist.")
		);
		return userStorage.update(user);
	}

	@Override
	public List<User> getUsers() {
		return userStorage.getAll();
	}

	@Override
	public User addFriend(int userId, int friendId) {
		User user = userStorage.get(userId).orElseThrow(
				() -> new UserNotExistException("User with id " + userId + " isn't exist.")
		);
		User friend = userStorage.get(friendId).orElseThrow(
				() -> new UserNotExistException("User with id " + friendId + " isn't exist.")
		);
		if (user.getFriends() == null)
			user.setFriends(new HashSet<>());
		if (friend.getFriends() == null)
			friend.setFriends(new HashSet<>());
		user.getFriends().add(friendId);
		friend.getFriends().add(userId);
		return user;
	}

	@Override
	public User removeFriend(int userId, int friendId) {
		User user = userStorage.get(userId).orElseThrow(
				() -> new UserNotExistException("User with id " + userId + " isn't exist.")
		);
		User friend = userStorage.get(friendId).orElseThrow(
				() -> new UserNotExistException("User with id " + friendId + " isn't exist.")
		);

		if (user.getFriends() == null || user.getFriends().isEmpty() || !user.getFriends().contains(friendId))
			throw new UsersAreNotFriendsException("Users with id " + userId + " and " + friendId + " are not friends.");
		user.getFriends().remove(friendId);
		friend.getFriends().remove(userId);
		return user;
	}

	@Override
	public List<User> getFriends(int userId) {
		User user = userStorage.get(userId).orElseThrow(
				() -> new UserNotExistException("User with id " + userId + " isn't exist.")
		);
		Set<Integer> friends = user.getFriends();
		if (friends == null || friends.isEmpty())
			return new ArrayList<>();
		return userStorage.get(new ArrayList<>(friends));
	}

	@Override
	public List<User> getMutualFriends(int userId, int otherUserId) {
		User user = userStorage.get(userId).orElseThrow(
				() -> new UserNotExistException("User with id " + userId + " isn't exist.")
		);
		User otherUser = userStorage.get(otherUserId).orElseThrow(
				() -> new UserNotExistException("User with id " + otherUserId + " isn't exist.")
		);
		Set<Integer> userFriends = user.getFriends();
		Set<Integer> otherUserFriends = otherUser.getFriends();
		if (userFriends == null || otherUserFriends == null || userFriends.isEmpty() || otherUserFriends.isEmpty())
			return new ArrayList<>();
		List<Integer> mutualFriends = userFriends.stream()
				.filter(otherUserFriends::contains)
				.collect(Collectors.toList());
		return userStorage.get(mutualFriends);
	}

	private void setUserName(User user) {
		String name = user.getName();
		if (name == null || name.isBlank() || name.isEmpty()) {
			user.setName(user.getLogin());
			log.debug("Set user's name on [{}]", user.getName());
		}
	}
}
