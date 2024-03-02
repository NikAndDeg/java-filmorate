package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

public interface UserStorage extends Storage<User> {
	@Override
	User save(User user);

	@Override
	Optional<User> get(int userId);

	@Override
	List<User> get(List<Integer> usersId);

	@Override
	List<User> getAll();

	@Override
	Optional<User> remove(int userId);

	@Override
	User update(User user);

	@Override
	boolean contains(User user);

	@Override
	boolean contains(int userId);
}
