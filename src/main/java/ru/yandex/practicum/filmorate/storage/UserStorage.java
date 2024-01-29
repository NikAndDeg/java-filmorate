package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

public interface UserStorage extends Storage<User> {
	@Override
	User save(User user);

	@Override
	Optional<User> get(int index);

	@Override
	List<User> get(List<Integer> indexes);

	@Override
	List<User> getAll();

	@Override
	Optional<User> remove(int index);

	@Override
	User update(User user);

	@Override
	boolean contains(User user);

	boolean containsEmail(User user);

	boolean containsName(User user);

	boolean containsLogin(User user);
}
