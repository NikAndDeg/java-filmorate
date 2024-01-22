package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import ru.yandex.practicum.filmorate.validator.ReleaseDateValidation;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

import java.time.LocalDate;

@Data
@Builder(toBuilder = true)
public class Film {
	@EqualsAndHashCode.Exclude
	private int id;
	@NotBlank
	@Size(max = 200)
	private String name;
	@Size(max = 200)
	private String description;
	@EqualsAndHashCode.Exclude
	@ReleaseDateValidation
	private LocalDate releaseDate;
	@EqualsAndHashCode.Exclude
	@Positive
	private int duration;
}
