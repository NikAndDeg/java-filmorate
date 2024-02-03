package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.user.UserService;

import javax.validation.Valid;

import java.util.*;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
	UserService userService;

	@Autowired
	public UserController(@Qualifier("UserServiceImpl") UserService userService) {
		this.userService = userService;
	}

	@PostMapping
	public User postUser(@Valid @RequestBody User user) {
		log.info("Request to post user [{}]", user);
		return userService.addUser(user);
	}

	@PutMapping
	public User putUser(@Valid @RequestBody User user) {
		log.info("Request to put user [{}]", user);
		return userService.updateUser(user);
	}

	@GetMapping
	public List<User> getUsers() {
		log.info("Request to get users.");
		return userService.getUsers();
	}

	@GetMapping("/{id}")
	public User getUser(@PathVariable("id") int userId) {
		log.info("Request to get user with id [{}]", userId);
		return userService.getUser(userId);
	}

	@PutMapping("/{id}/friends/{friendId}")
	public User addFriend(@PathVariable("id") int userId, @PathVariable int friendId) {
		log.info("Request to add user [{}] to user's [{}] friend list.", friendId, userId);
		return userService.addFriend(userId, friendId);
	}

	@DeleteMapping("/{id}/friends/{friendId}")
	public User removeFriend(@PathVariable("id") int userId, @PathVariable int friendId) {
		log.info("Request to remove user [{}] from user's [{}] friend list.", friendId, userId);
		return userService.removeFriend(userId, friendId);
	}

	@GetMapping("/{id}/friends")
	public List<User> getFriends(@PathVariable("id") int userId) {
		log.info("Request to get user's [{}] friends.", userId);
		return userService.getFriends(userId);
	}

	@GetMapping("/{id}/friends/common/{otherId}")
	public List<User> getMutualFriends(@PathVariable("id") int userId, @PathVariable int otherId) {
		log.info("Request to get mutual friends between user [{}] and [{}]", userId, otherId);
		return userService.getMutualFriends(userId, otherId);
	}
}
