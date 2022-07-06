drop table if exists
 genres,rate_mpa,films,film_genres,FILM_DIRECTOR,DIRECTORS,user_user,rate_users,user_friends, reviews;

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
  film_id BIGINT,
  genre_id INTEGER,
  constraint FILM_GENRES_FILMS_FILM_ID_FK
      foreign key (FILM_ID) references FILMS (FILM_ID) ON DELETE CASCADE,
  constraint FILM_GENRES_GENRES_GENRE_ID_FK
      foreign key (GENRE_ID) references GENRES (GENRE_ID) ON DELETE CASCADE
 );

create table IF NOT EXISTS DIRECTORS
(
    DIRECTOR_ID   INT auto_increment,
    DIRECTOR_NAME CHARACTER VARYING(100),
    constraint DIRECTOR_FILM_PK
        primary key (DIRECTOR_ID)
);

create table IF NOT EXISTS FILM_DIRECTOR
(
    FILM_ID     BIGINT not null,
    DIRECTOR_ID INT    not null,
    CONSTRAINT pkFilmDirector PRIMARY KEY (FILM_ID, DIRECTOR_ID),
        foreign key (DIRECTOR_ID) references DIRECTORS (DIRECTOR_ID) ON DELETE CASCADE,
        foreign key (FILM_ID) references FILMS (FILM_ID) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS user_user (
 user_name VARCHAR(50),
 user_login VARCHAR(50) NOT NULL UNIQUE,
 user_id BIGINT PRIMARY KEY AUTO_INCREMENT,
 user_email VARCHAR(50),
 user_birthday VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS rate_users (
 film_id BIGINT,
 user_id BIGINT,
 constraint RATE_USERS_FILMS_FILM_ID_FK
     foreign key (FILM_ID) references FILMS (FILM_ID) ON DELETE CASCADE,
 constraint RATE_USERS_USER_USER_USER_ID_FK
     foreign key (USER_ID) references USER_USER (USER_ID) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS user_friends (
 user_id BIGINT,
 friend_id BIGINT,
 friend_status BOOlEAN,
  constraint FRIENDS_USERS_USER_ID_FK
      foreign key (USER_ID) references user_user ON DELETE CASCADE,
  foreign key (FRIEND_ID) references user_user ON DELETE CASCADE
);