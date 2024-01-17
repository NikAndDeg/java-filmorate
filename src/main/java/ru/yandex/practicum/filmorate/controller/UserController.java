package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import ru.yandex.practicum.filmorate.exception.UserAlreadyAddedException;
import ru.yandex.practicum.filmorate.exception.UserNotExistException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;

import java.util.*;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
	private final Map<Integer, User> users = new HashMap<>();
	private final Set<String> emails = new TreeSet<>();
	private final Set<String> names = new TreeSet<>();
	private final Set<String> logins = new TreeSet<>();
	private int idCounter = 0;

	@PostMapping
	public User postUser(@Valid @RequestBody User user) {
		log.info("Post [" + user.toString() + "].");
		setUserName(user);
		userExistCheck(user);
		addUser(user);
		safeUserInfo(user);
		log.info("User posted successfully. Users size [" + users.size() + "].");
		return user;
	}

	@PutMapping
	public User putUser(@Valid @RequestBody User user) {
		log.info("Put [" + user.toString() + "].");
		removeUser(user);
		users.put(user.getId(), user);
		safeUserInfo(user);
		log.info("User put successfully");
		return user;
	}

	@GetMapping
	public List<User> getUsers() {
		return new ArrayList<>(users.values());
	}

	private void setUserName(User user) {
		String name = user.getName();
		if (name == null || name.isBlank() || name.isEmpty())
			user.setName(user.getLogin());
	}

	private void userExistCheck(User user) {
		if (emails.contains(user.getEmail()))
			throw new UserAlreadyAddedException("User with email " + user.getEmail() + " is already exist.");
		if (names.contains(user.getName()))
			throw new UserAlreadyAddedException("User with name " + user.getName() + " is already exist.");
		if (logins.contains(user.getLogin()))
			throw new UserAlreadyAddedException("User with login " + user.getLogin() + " is already exist");
	}

	private void addUser(User user) {
		user.setId(++idCounter);
		users.put(user.getId(), user);
	}

	private void safeUserInfo(User user) {
		emails.add(user.getEmail());
		names.add(user.getName());
		logins.add(user.getLogin());
	}

	private void removeUser(User user) {
		log.debug("Remove [" + user.toString() + "].");
		int userId = user.getId();
		User deletedUser = users.remove(userId);
		if (deletedUser == null)
			throw new UserNotExistException("User with id " + userId + " isn't exist.");
		deleteUserInfo(deletedUser);
		log.debug("User removed.");
	}

	private void deleteUserInfo(User user) {
		log.debug("Delete user's email, name and login.");
		emails.remove(user.getEmail());
		names.remove(user.getName());
		logins.remove(user.getLogin());
	}
}
