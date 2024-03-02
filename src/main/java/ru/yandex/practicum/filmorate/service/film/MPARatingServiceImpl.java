package ru.yandex.practicum.filmorate.service.film;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.MPARatingException;
import ru.yandex.practicum.filmorate.model.MPARating;
import ru.yandex.practicum.filmorate.storage.dao.film.MPARatingDao;

import java.util.Set;

@Service
public class MPARatingServiceImpl implements MPARatingService {
	private final MPARatingDao ratingDao;

	@Autowired
	public MPARatingServiceImpl(MPARatingDao ratingDao) {
		this.ratingDao = ratingDao;
	}

	@Override
	public Set<MPARating> getAllMPARatings() {
		return ratingDao.getAllMPARatings();
	}

	@Override
	public MPARating getMPARatingByRatingId(int mpaRatingId) {
		return ratingDao.getMPARatingByRatingId(mpaRatingId).orElseThrow(
				() -> new MPARatingException("Incorrect rating id.")
		);
	}
}
