DROP TABLE IF EXISTS films CASCADE;
DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS films_likes CASCADE;
DROP TABLE IF EXISTS users_friends CASCADE;
DROP TABLE IF EXISTS genres CASCADE;
DROP TABLE IF EXISTS mpa_ratings CASCADE;
DROP TABLE IF EXISTS films_genres CASCADE;

CREATE TABLE mpa_ratings (
    mpa_rating_id INT AUTO_INCREMENT,
    mpa_rating_name VARCHAR(200),

    PRIMARY KEY(mpa_rating_id)
);

CREATE TABLE films (
    film_id INT AUTO_INCREMENT,
    film_name VARCHAR(200) NOT NULL,
    description VARCHAR(200),
    release_date DATE NOT NULL,
    duration INT NOT NULL,
    mpa_rating_id INT NOT NULL,

    PRIMARY KEY(film_id),
    FOREIGN KEY(mpa_rating_id) REFERENCES mpa_ratings(mpa_rating_id)
);

CREATE TABLE genres (
    genre_id INT AUTO_INCREMENT,
    genre_name VARCHAR(200),

    PRIMARY KEY(genre_id)
);

CREATE TABLE films_genres (
    fg_id INT AUTO_INCREMENT,
    film_id INT NOT NULL,
    genre_id INT NOT NULL,

    PRIMARY KEY(fg_id),
    FOREIGN KEY(film_id) REFERENCES films(film_id) ON DELETE CASCADE,
    FOREIGN KEY(genre_id) REFERENCES genres(genre_id) ON DELETE CASCADE
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

INSERT INTO genres (genre_name) VALUES ('Комедия');
INSERT INTO genres (genre_name) VALUES ('Драма');
INSERT INTO genres (genre_name) VALUES ('Мультфильм');
INSERT INTO genres (genre_name) VALUES ('Триллер');
INSERT INTO genres (genre_name) VALUES ('Документальный');
INSERT INTO genres (genre_name) VALUES ('Боевик');

INSERT INTO mpa_ratings (mpa_rating_name) VALUES ('G');
INSERT INTO mpa_ratings (mpa_rating_name) VALUES ('PG');
INSERT INTO mpa_ratings (mpa_rating_name) VALUES ('PG-13');
INSERT INTO mpa_ratings (mpa_rating_name) VALUES ('R');
INSERT INTO mpa_ratings (mpa_rating_name) VALUES ('NC-17');

INSERT INTO films (film_name, description, release_date, duration, mpa_rating_id)
VALUES ('1 Film', 'Description', '2001-01-01', 123, 1),
       ('2 Film', 'Description', '2002-02-03', 123, 2),
       ('3 Film', 'Description', '2003-03-03', 123, 3),
       ('4 Film', 'Description', '2004-04-04', 123, 4);

INSERT INTO users (email, login, user_name, birthday)
VALUES ('1mail.com', '1login', '1name', '2001-01-01'),
       ('2mail.com', '2login', '2name', '2001-01-01'),
       ('3mail.com', '3login', '3name', '2001-01-01'),
       ('4mail.com', '4login', '4name', '2001-01-01'),
       ('5mail.com', '5login', '5name', '2001-01-01');