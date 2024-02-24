DROP TABLE IF EXISTS films CASCADE;
DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS films_likes CASCADE;
DROP TABLE IF EXISTS users_friends CASCADE;

CREATE TABLE films (
    film_id INT AUTO_INCREMENT,
    film_name VARCHAR(200) NOT NULL,
    description VARCHAR(200),
    release_date DATE NOT NULL,
    duration INT NOT NULL,

    PRIMARY KEY(film_id)
);

CREATE TABLE users (
    user_id INT AUTO_INCREMENT,
    email VARCHAR(200) NOT NULL UNIQUE,
    login VARCHAR(200) NOT NULL UNIQUE,
    user_name VARCHAR(200) NOT NULL,
    birthday DATE NOT NULL,

    PRIMARY KEY(user_id)
);

CREATE TABLE films_likes (
    fl_id INT AUTO_INCREMENT,
    film_id INT NOT NULL,
    user_id INT NOT NULL,

    PRIMARY KEY(fl_id),
    FOREIGN KEY(film_id) REFERENCES films(film_id) ON DELETE CASCADE,
    FOREIGN KEY(user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

CREATE TABLE users_friends (
    uf_id INT AUTO_INCREMENT,
    user_id INT NOT NULL,
    friend_id INT NOT NULL,

    PRIMARY KEY(uf_id),
    FOREIGN KEY(user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY(friend_id) REFERENCES users(user_id) ON DELETE CASCADE
);

