package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Component
@Qualifier("InMemoryUserStorage")
@Slf4j
public class InMemoryUserStorage implements UserStorage {
	private final Map<Integer, User> users = new HashMap<>();
	private final Set<String> emails = new TreeSet<>();
	private final Set<String> names = new TreeSet<>();
	private final Set<String> logins = new TreeSet<>();
	private int indexCounter = 0;

	@Override
	public User save(User user) {
		setIndex(user);
		saveUserInfo(user);
		users.put(user.getId(), user);
		log.info("User saved. Users size [{}].", users.size());
		return user;
	}

	@Override
	public Optional<User> get(int index) {
		return Optional.ofNullable(users.get(index));
	}

	@Override
	public List<User> get(List<Integer> indexes) {
		return indexes.stream()
				.map(users::get)
				.collect(Collectors.toList());
	}

	@Override
	public List<User> getAll() {
		return new ArrayList<>(users.values());
	}

	@Override
	public Optional<User> remove(int index) {
		Optional<User> removedUser = Optional.ofNullable(users.remove(index));
		removedUser.ifPresent(this::removeUserInfo);
		return removedUser;
	}

	@Override
	public User update(User user) {
		User oldUser = users.get(user.getId());
		updateUserInfo(oldUser, user);
		users.put(user.getId(), user);
		return user;
	}

	@Override
	public boolean contains(User user) {
		return users.containsValue(user);
	}

	@Override
	public boolean containsEmail(User user) {
		return emails.contains(user.getEmail());
	}

	@Override
	public boolean containsName(User user) {
		return names.contains(user.getName());
	}

	@Override
	public boolean containsLogin(User user) {
		return logins.contains(user.getLogin());
	}

	private void setIndex(User user) {
		user.setId(++indexCounter);
		log.debug("Set user's id to [{}]", user.getId());
	}

	private void saveUserInfo(User user) {
		log.debug("Saving user's email, name and login");
		emails.add(user.getEmail());
		names.add(user.getName());
		logins.add(user.getLogin());
	}

	private void removeUserInfo(User user) {
		log.debug("Removing user's email, name and login");
		emails.remove(user.getEmail());
		names.remove(user.getName());
		logins.remove(user.getLogin());
	}

	private void updateUserInfo(User oldUser, User user) {
		log.debug("Updating user's email, name and login");
		updateUserEmailInfo(oldUser, user);
		updateUserNameInfo(oldUser, user);
		updateUserLoginInfo(oldUser, user);
	}

	private void updateUserEmailInfo(User oldUser, User user) {
		String oldEmail = oldUser.getEmail();
		String email = user.getEmail();
		if (oldEmail.equals(email))
			return;
		emails.remove(oldEmail);
		emails.add(email);
		log.debug("User's new email is saved.");
	}

	private void updateUserNameInfo(User oldUser, User user) {
		String oldName = oldUser.getName();
		String name = user.getName();
		if (oldName.equals(name))
			return;
		names.remove(oldName);
		names.add(name);
		log.debug("User's new name is saved.");
	}

	private void updateUserLoginInfo(User oldUser, User user) {
		String oldLogin = oldUser.getLogin();
		String login = user.getLogin();
		if (oldLogin.equals(login))
			return;
		logins.remove(oldLogin);
		logins.add(login);
		log.debug("User's new login is saved.");
	}
}
