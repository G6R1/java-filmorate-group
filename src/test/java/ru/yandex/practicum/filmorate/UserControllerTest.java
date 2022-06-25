package ru.yandex.practicum.filmorate;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.model.User;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;
    private static ObjectMapper mapper = new ObjectMapper();

    @Test
    void addUserTestCorrectUser() throws Exception {
        User user = new User();
        user.setLogin("UserTest");
        user.setName("TestUser");
        user.setEmail("example@email.email");
        user.setBirthday("2001-11-11");
        String json = mapper.writeValueAsString(user);
        mockMvc.perform(post("/users").content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void addUserTestBlankUserName() throws Exception {
        User user = new User();
        user.setLogin("UserTest");
        user.setEmail("example@email.email");
        user.setBirthday("2001-11-11");
        String json = mapper.writeValueAsString(user);
        mockMvc.perform(post("/users").content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void addUserTestIncorrectUserEmail() throws Exception {
        User user = new User();
        user.setLogin("UserTest");
        user.setName("TestUser");
        user.setEmail("example email.email");
        user.setBirthday("2001-11-11");
        String json = mapper.writeValueAsString(user);
        mockMvc.perform(post("/users").content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addUserTestIncorrectUserLogin() throws Exception {
        User user = new User();
        user.setLogin("User Test");
        user.setName("TestUser");
        user.setEmail("example@email.email");
        user.setBirthday("2001-11-11");
        String json = mapper.writeValueAsString(user);
        mockMvc.perform(post("/users").content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addUserTestIncorrectUserBirthday() throws Exception {
        User user = new User();
        user.setLogin("UserTest");
        user.setName("TestUser");
        user.setEmail("example@email.email");
        user.setBirthday("2201-11-11");
        String json = mapper.writeValueAsString(user);
        mockMvc.perform(post("/users").content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}