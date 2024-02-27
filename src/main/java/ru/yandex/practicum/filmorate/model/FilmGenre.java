package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Positive;

@Data
@Builder
public class FilmGenre {
	@Positive
	private int id;
	@EqualsAndHashCode.Exclude
	private String name;
}
