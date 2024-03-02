package ru.yandex.practicum.filmorate.service.film;

import ru.yandex.practicum.filmorate.model.MPARating;

import java.util.Set;

public interface MPARatingService {
	Set<MPARating> getAllMPARatings();

	public MPARating getMPARatingByRatingId(int mpaRatingId);
}
