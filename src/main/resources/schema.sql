CREATE TABLE if not exists MPA
(
    mpa_id   INTEGER AUTO_INCREMENT PRIMARY KEY NOT NULL,
    mpa_name VARCHAR                            NOT NULL
);
CREATE TABLE IF NOT EXISTS films
(
    film_id           INTEGER AUTO_INCREMENT PRIMARY KEY NOT NULL,
    film_name         TEXT                               NOT NULL,
    film_description  VARCHAR(200)                       NOT NULL,
    film_release_date DATE                               NOT NULL,
    film_duration     INTEGER                            NOT NULL,
    film_rate         INTEGER,
    film_mpa          INTEGER                            NOT NULL,
    CONSTRAINT uk_films_name_rel_dur UNIQUE (film_name, film_release_date, film_duration),
    FOREIGN KEY (film_mpa) REFERENCES mpa (mpa_id) ON DELETE RESTRICT ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS genres
(
    genre_id   INTEGER AUTO_INCREMENT PRIMARY KEY NOT NULL,
    genre_name VARCHAR                            NOT NULL
);

CREATE TABLE IF NOT EXISTS film_genres
(
    genre_id INTEGER NOT NULL,
    film_id  INTEGER NOT NULL,
    PRIMARY KEY (genre_id, film_id),
    FOREIGN KEY (genre_id) REFERENCES genres (genre_id) ON DELETE RESTRICT ON UPDATE CASCADE,
    FOREIGN KEY (film_id) REFERENCES films (film_id) ON DELETE RESTRICT ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS users
(
    user_id       INTEGER AUTO_INCREMENT PRIMARY KEY NOT NULL,
    user_login    VARCHAR                            NOT NULL,
    user_email    VARCHAR                            NOT NULL,
    user_name     VARCHAR                            NOT NULL,
    user_birthday DATE                               NOT NULL,
    UNIQUE (user_login),
    UNIQUE (user_email)

);

CREATE TABLE IF NOT EXISTS friendships
(
    user_id           INTEGER NOT NULL,
    friend_id         INTEGER NOT NULL,
    friendship_status boolean NOT NULL,
    PRIMARY KEY (user_id, friend_id),
    CONSTRAINT unique_friendship UNIQUE (user_id, friend_id, friendship_status),
    FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE RESTRICT ON UPDATE CASCADE,
    FOREIGN KEY (friend_id) REFERENCES users (user_id) ON DELETE RESTRICT ON UPDATE CASCADE

);

CREATE TABLE IF NOT EXISTS likes
(
    film_id INTEGER NOT NULL,
    user_id INTEGER NOT NULL,
    PRIMARY KEY (film_id, user_id),
    FOREIGN KEY (film_id) REFERENCES films (film_id) ON DELETE RESTRICT ON UPDATE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE RESTRICT ON UPDATE CASCADE
);
