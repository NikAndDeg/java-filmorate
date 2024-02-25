package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.MPARating;
import ru.yandex.practicum.filmorate.service.film.MPARatingService;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/mpa")
public class MPARatingController {
	private final MPARatingService ratingService;

	@Autowired
	public MPARatingController(MPARatingService ratingService) {
		this.ratingService = ratingService;
	}

	@GetMapping()
	public List<MPARating> getAllMPARatings() {
		List<MPARating> ratings = new ArrayList<>(ratingService.getAllMPARatings());
		ratings.sort(Comparator.comparingInt(MPARating::getId));
		return ratings;
	}

	@GetMapping("/{id}")
	public MPARating getMPARatingByRatingId(@PathVariable int id) {
		return ratingService.getMPARatingByRatingId(id);
	}
}
