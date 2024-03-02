package ru.yandex.practicum.filmorate.handler;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import ru.yandex.practicum.filmorate.exception.*;

@RestControllerAdvice("ru.yandex.practicum.filmorate")
public class ErrorHandler {
	@ExceptionHandler
	@ResponseStatus(HttpStatus.ALREADY_REPORTED)
	public ErrorResponse handleFilmAlreadyAdded(final FilmAlreadyAddedException exp) {
		return new ErrorResponse("error", exp.getMessage());
	}

	@ExceptionHandler
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ErrorResponse handleFilmNotFound(final FilmNotExistException exp) {
		return new ErrorResponse("error", exp.getMessage());
	}

	@ExceptionHandler
	@ResponseStatus(HttpStatus.ALREADY_REPORTED)
	public ErrorResponse handleLikeAlreadyAdded(final LikeAlreadyAddedException exp) {
		return new ErrorResponse("error", exp.getMessage());
	}

	@ExceptionHandler
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ErrorResponse handleLikeNotFound(final LikeNotFoundException exp) {
		return new ErrorResponse("error", exp.getMessage());
	}

	@ExceptionHandler
	@ResponseStatus(HttpStatus.ALREADY_REPORTED)
	public ErrorResponse handleUserAlreadyExist(final UserAlreadyAddedException exp) {
		return new ErrorResponse("error", exp.getMessage());
	}

	@ExceptionHandler
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ErrorResponse handleUserNotFound(final UserNotExistException exp) {
		return new ErrorResponse("error", exp.getMessage());
	}

	@ExceptionHandler
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ErrorResponse handleFriendshipNotFound(final UsersAreNotFriendsException exp) {
		return new ErrorResponse("error", exp.getMessage());
	}

	@ExceptionHandler
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ErrorResponse handleFilmGenreException(final FilmGenreException exp) {
		return new ErrorResponse("error", exp.getMessage());
	}

	@ExceptionHandler
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ErrorResponse handleMPARatingException(final MPARatingException exp) {
		return new ErrorResponse("error", exp.getMessage());
	}
}
