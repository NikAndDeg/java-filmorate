package ru.yandex.practicum.filmorate.storage.dao.film;

import ru.yandex.practicum.filmorate.model.MPARating;

import java.util.Optional;
import java.util.Set;

public interface MPARatingDao {
	Set<MPARating> getAllMPARatings();
	Optional<MPARating> getMPARatingByRatingId(int mpaRatingId);
}
