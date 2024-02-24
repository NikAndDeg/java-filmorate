package ru.yandex.practicum.filmorate.storage.dao.film;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface FilmLikesDao {
	void save(int filmId, Set<Integer> likes);
	Set<Integer> get(int filmId);
	Map<Integer, Set<Integer>> get(List<Integer> filmsId);
	Map<Integer, Set<Integer>> getAll();
	void add(int filmId, int userId);
	void remove(int filmId, int userId);
	List<Integer> getMostLikedFilmsId(int size);
}
