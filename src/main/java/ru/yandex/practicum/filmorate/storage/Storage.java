package ru.yandex.practicum.filmorate.storage;

import java.util.List;
import java.util.Optional;

public interface Storage<T> {
	T save(T t);

	Optional<T> get(int index);

	List<T> get(List<Integer> indexes);

	List<T> getAll();

	Optional<T> remove(int index);

	T update(T t);

	boolean contains(T t);

	boolean contains(int index);
}
