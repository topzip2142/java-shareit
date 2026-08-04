package ru.practicum.shareit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.UserClient;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private UserClient userClient;

    @Test
    void getUser() throws Exception {
        Map<String, Object> userResponse = Map.of(
                "id", 1,
                "name", "Иван",
                "email", "test@mail.com"
        );

        when(userClient.getUser(anyLong()))
                .thenReturn(ResponseEntity.ok(userResponse));

        mvc.perform(get("/users/{id}", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Иван")))
                .andExpect(jsonPath("$.email", is("test@mail.com")));
    }

    @Test
    void getUsers() throws Exception {
        Map<String, Object> userResponse = Map.of(
                "id", 1,
                "name", "Иван",
                "email", "test@mail.com"
        );

        when(userClient.getUsers())
                .thenReturn(ResponseEntity.ok(List.of(userResponse)));

        mvc.perform(get("/users")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("Иван")));
    }

    @Test
    void createUser() throws Exception {
        NewUserRequest request = new NewUserRequest();
        request.setName("Иван");
        request.setEmail("test@mail.com");

        Map<String, Object> userResponse = Map.of(
                "id", 1,
                "name", "Иван",
                "email", "test@mail.com"
        );

        when(userClient.createUser(any(NewUserRequest.class)))
                .thenReturn(ResponseEntity.ok(userResponse));

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(request))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Иван")))
                .andExpect(jsonPath("$.email", is("test@mail.com")));
    }

    @Test
    void updateUser() throws Exception {
        UpdateUserRequest request = new UpdateUserRequest();
        request.setName("Иван Сильвер");

        Map<String, Object> userResponse = Map.of(
                "id", 1,
                "name", "Иван Сильвер",
                "email", "test@mail.com"
        );

        when(userClient.updateUser(anyLong(), any(UpdateUserRequest.class)))
                .thenReturn(ResponseEntity.ok(userResponse));

        mvc.perform(patch("/users/{id}", 1L)
                        .content(mapper.writeValueAsString(request))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Иван Сильвер")));
    }

    @Test
    void deleteUser() throws Exception {
        when(userClient.delUser(anyLong()))
                .thenReturn(ResponseEntity.ok().build());

        mvc.perform(delete("/users/{id}", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}