package ru.yandex.practicum.filmorate.exception;

public class FilmAlreadyAddedException extends RuntimeException {
	public FilmAlreadyAddedException(String message) {
		super(message);
	}
}
