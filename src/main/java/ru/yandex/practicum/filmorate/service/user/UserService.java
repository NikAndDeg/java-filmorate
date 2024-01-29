package ru.yandex.practicum.filmorate.service.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserService {
	User addUser(User user);

	User getUser(int userId);

	User updateUser(User user);

	List<User> getUsers();

	User addFriend(int userId, int friendId);

	User removeFriend(int userId, int friendId);

	List<User> getFriends(int userId);

	List<User> getMutualFriends(int userId, int otherUserId);
}
