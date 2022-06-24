# java-filmorate
# java-filmorate-er
![java-filmorate-er (3)_LI](https://user-images.githubusercontent.com/97310053/172790238-ff9bda0b-7fb4-4ce1-bbb6-3614ce63f72b.jpg)

CREATE TABLE IF NOT EXISTS Film (
 film_id PRIMARY KEY AUTO_INCREMENT,
 film_name varchar(64) NOT NULL,
 film_description varchar(200),
 film_release_date DATE NOT NULL CHECK (film_release_date > '1895-12-28'),
 film_duration INTEGER NOT NULL,
 film_rate_mpa INTEGER,
 film_rate_users INTEGER,
 film_genre INTEGER
 );

CREATE TABLE IF NOT EXISTS FilmGenre (
 genre_id INTEGER,
 film_id INTEGER
 );

CREATE TABLE IF NOT EXISTS Genre (
 genre_id INTEGER PRIMARY KEY,
 genre_name varchar
 );

CREATE TABLE IF NOT EXISTS RateMPA (
 mpa_id INTEGER PRIMARY KEY,
 mpa_name varchar,
 mpa_description varchar
 );

CREATE TABLE IF NOT EXISTS RateUsers (
 user_id INTEGER,
 film_id INTEGER
 );

CREATE TABLE IF NOT EXISTS UserUser (
 user_login VARCHAR(50) NOT NULL,
 user_name VARCHAR(50),
 user_id PRIMARY KEY AUTO_INCREMENT,
 user_email VARCHAR(50) CHECK (user_email REGEXP '^[^@]+@[^@]+\\.[^@]{2,}$'),
 user_birthday DATE NOT NULL CHECK (birthday < CURRENT_DATE),
 friends INTEGER
 );

CREATE TABLE IF NOT EXISTS UserFriends (
 user_id INTEGER,
 friend_id INTEGER,
 friend_status INTEGER
 );

ALTER TABLE RateMPA ADD FOREIGN KEY (mpa_id) REFERENCES Film (film_rate_mpa);

ALTER TABLE RateUsers ADD FOREIGN KEY (film_id) REFERENCES Film (film_rate_users);

ALTER TABLE FilmGenre ADD FOREIGN KEY (film_id) REFERENCES Film (film_genre);

ALTER TABLE Genre ADD FOREIGN KEY (genre_id) REFERENCES FilmGenre (genre_id);

ALTER TABLE UserFriends ADD FOREIGN KEY (user_id) REFERENCES User (friends);
