CREATE TABLE IF NOT EXISTS genres (
  genre_id INTEGER PRIMARY KEY,
  genre_name VARCHAR(50)
  );

CREATE TABLE IF NOT EXISTS rate_mpa (
 mpa_id INTEGER PRIMARY KEY,
 mpa_name VARCHAR(50),
 mpa_description VARCHAR(200)
);

CREATE TABLE IF NOT EXISTS films (
 film_id BIGINT PRIMARY KEY AUTO_INCREMENT,
 film_name VARCHAR(50) NOT NULL,
 film_release_date DATE NOT NULL CHECK (film_release_date > '1895-12-28'),
 film_description VARCHAR(200),
 film_duration INTEGER NOT NULL,
 mpa_id INTEGER,
 CONSTRAINT fk_mpa FOREIGN KEY (mpa_id) REFERENCES  rate_mpa (mpa_id)
 );

 CREATE TABLE IF NOT EXISTS film_genres (
  film_id BIGINT REFERENCES films (film_id),
  genre_id INTEGER REFERENCES genres (genre_id)
 );

CREATE TABLE IF NOT EXISTS user_user (
 user_name VARCHAR(50),
 user_login VARCHAR(50) NOT NULL UNIQUE,
 user_id BIGINT PRIMARY KEY AUTO_INCREMENT,
 user_email VARCHAR(50),
 user_birthday VARCHAR(50) NOT NULL
 );

CREATE TABLE IF NOT EXISTS rate_users (
 film_id BIGINT REFERENCES films (film_id),
 user_id BIGINT REFERENCES user_user(user_id)
);

CREATE TABLE IF NOT EXISTS user_friends (
 user_id BIGINT REFERENCES user_user (user_id),
 friend_id BIGINT,
 friend_status BOOlEAN
);