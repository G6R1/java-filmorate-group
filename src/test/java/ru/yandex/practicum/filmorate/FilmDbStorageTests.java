package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.embedded.undertow.UndertowServletWebServer;
import ru.yandex.practicum.filmorate.dao.FilmStorage;
import ru.yandex.practicum.filmorate.dao.UserStorage;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.RateMpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.RateUserDbStorage;
import ru.yandex.practicum.filmorate.storage.UserDbStorage;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmDbStorageTests {
    private final FilmDbStorage filmDbStorage;
    private final UserDbStorage userStorage;
    private final RateUserDbStorage rateUserDbStorage;

    @Test
    void testGetTopFilms() {
        Random random = new Random();
        for (int i = 5; i >= 0; i--) {
            User user = new User();
            user.setLogin(String.valueOf(random.nextLong()));
            user.setName(String.valueOf(random.nextLong()));
            user.setEmail(String.valueOf(random.nextLong()));
            user.setBirthday("2001-11-11");
            userStorage.createUser(user);
        }

        for (int i = 5; i >= 1; i--) {
            Film film = new Film();
            film.setDescription(String.valueOf(random.nextLong()));
            film.setName(String.valueOf(random.nextLong()));
            Genre comedy = new Genre();
            comedy.setId(1);
            film.setGenres(Set.of(comedy));
            RateMpa rate = new RateMpa();
            rate.setId(1);
            film.setReleaseDate("2001-11-11");
            film.setDuration(100);
            film.setMpa(rate);
            filmDbStorage.createFilm(film);
            for (int j = 5; j >= i; j--) {
              rateUserDbStorage.addRateUser(film.getId(), j);
            }
        }

        List<Film> result = filmDbStorage.getTopFilms(10, 0, -1);
        System.out.println(result);
        assertThat(result).isNotEmpty();
    }


}