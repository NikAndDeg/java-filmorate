package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Data;

import ru.yandex.practicum.filmorate.validator.ReleaseDateValidation;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

import java.time.LocalDate;
import java.util.Set;

@Data
@Builder(toBuilder = true)
public class Film {
	@EqualsAndHashCode.Exclude
	private int id;
	@NotBlank(message = "must not be blank")
	@Size(max = 200, message = "size must be between 0 and 200")
	private String name;
	@Size(max = 200, message = "size must be between 0 and 200")
	private String description;
	@EqualsAndHashCode.Exclude
	@ReleaseDateValidation
	private LocalDate releaseDate;
	@EqualsAndHashCode.Exclude
	@Positive(message = "must be greater than 0")
	private int duration;
	@EqualsAndHashCode.Exclude
	private Set<Integer> likes;
	@EqualsAndHashCode.Exclude
	private Set<FilmGenre> genres;
	@EqualsAndHashCode.Exclude
	private MPARating mpa;
}
