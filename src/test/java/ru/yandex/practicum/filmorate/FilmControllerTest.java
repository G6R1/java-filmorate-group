package ru.yandex.practicum.filmorate;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.model.Film;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
class FilmControllerTest {

    @Autowired
    private MockMvc mockMvc;
    private static ObjectMapper mapper = new ObjectMapper();

    @Test
    void addFilmTestCorrectFilm() throws Exception {
        Film film = new Film();
        film.setName("TestFilm");
        film.setDescription("FilmTest");
        film.setReleaseDate("1945-09-05");
        film.setDuration(30);
        String json = mapper.writeValueAsString(film);
        mockMvc.perform(post("/films").content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void addFilmTestBlankFilmName() throws Exception {
        Film film = new Film();
        film.setName("");
        film.setDescription("description");
        film.setReleaseDate("1945-09-05");
        film.setDuration(30);
        String json = mapper.writeValueAsString(film);
        mockMvc.perform(post("/films").content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addFilmTestFilmDescriptionMoreThen200() throws Exception {
        Film film = new Film();
        film.setName("TestFilm");
        film.setDescription("nodibnoidfboadibadoinbdoibaodibaopdibapdoib" +
                "jadopibpdaoijbpadoijbpadoijbpaddolikadoijaodijbaodijbaoid" +
                "jbnaoibnjldknfbmakljbnwpeouiwpeoutwpirutwperotwpoeuwpoeutwpoe" +
                "utpeoiruqewefsdglknvslfdkvnsldfkvnslfdvkslkvnslkdvfnfsdkvjxcvxcvb" +
                "xcbncx,vbmkncx,.vmnlfdkjsdgsdsdglkigjhdlkfgjbhdl;fkjb;sdfmvs;dlmcv");
        film.setReleaseDate("1945-09-05");
        film.setDuration(30);
        String json = mapper.writeValueAsString(film);
        mockMvc.perform(post("/films").content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addFilmTestIncorrectReleaseDate() throws Exception {
        Film film = new Film();
        film.setName("TestFilm");
        film.setDescription("description");
        film.setReleaseDate("1885-09-05");
        film.setDuration(30);
        String json = mapper.writeValueAsString(film);
        mockMvc.perform(post("/films").content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addFilmTestIncorrectDuration() throws Exception {
        Film film = new Film();
        film.setName("TestFilm");
        film.setDescription("description");
        film.setReleaseDate("1885-09-05");
        film.setDuration(-30);
        String json = mapper.writeValueAsString(film);
        mockMvc.perform(post("/films").content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}