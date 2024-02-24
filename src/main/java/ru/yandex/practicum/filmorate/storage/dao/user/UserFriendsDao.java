package ru.yandex.practicum.filmorate.storage.dao.user;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface UserFriendsDao {
	void save(int userId, Set<Integer> friends);
	Set<Integer> get(int userId);
	Map<Integer, Set<Integer>> get(List<Integer> usersId);
	Map<Integer, Set<Integer>> getAll();
	void add(int userId, int friendId);
	void remove(int userId, int friendId);
	List<Integer> getMutualFriend(int userId, int otherUserId);
}
