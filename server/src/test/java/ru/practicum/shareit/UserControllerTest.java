package ru.practicum.shareit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.NewUserRequestDTO;
import ru.practicum.shareit.user.dto.UpdateUserRequestDTO;
import ru.practicum.shareit.user.dto.UserResponseDTO;

import java.nio.charset.StandardCharsets;
import java.util.List;

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
    ObjectMapper mapper;

    @MockBean
    UserService service;

    @Autowired
    private MockMvc mvc;

    private final UserResponseDTO dto = new UserResponseDTO();

    @BeforeEach
    void beforeEach() {
        dto.setId(1L);
        dto.setName("Юзер");
        dto.setEmail("test@mail.com");
    }

    @Test
    void getUser() throws Exception {
        when(service.getUser(anyLong()))
                .thenReturn(dto);

        mvc.perform(get("/users/{userId}", dto.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(dto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(dto.getName())))
                .andExpect(jsonPath("$.email", is(dto.getEmail())));
    }

    @Test
    void getAllUsers() throws Exception {
        UserResponseDTO dto2 = new UserResponseDTO();
        dto2.setId(1L);
        dto2.setName("Юзер2");
        dto2.setEmail("test2@mail.com");

        when(service.getUsers())
                .thenReturn(List.of(dto, dto2));

        mvc.perform(get("/users")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(dto.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(dto.getName())))
                .andExpect(jsonPath("$[0].email", is(dto.getEmail())))

                .andExpect(jsonPath("$[1].id", is(dto2.getId()), Long.class))
                .andExpect(jsonPath("$[1].name", is(dto2.getName())))
                .andExpect(jsonPath("$[1].email", is(dto2.getEmail())));
    }

    @Test
    void updateUser() throws Exception {
        UpdateUserRequestDTO updateUserRequest = new UpdateUserRequestDTO();
        updateUserRequest.setEmail("new@mail.com");
        updateUserRequest.setName("Новое имя");

        dto.setEmail(updateUserRequest.getEmail());
        dto.setName(updateUserRequest.getName());

        when(service.updateUser(anyLong(), any(UpdateUserRequestDTO.class)))
                .thenReturn(dto);

        mvc.perform(patch("/users/{userId}", dto.getId())
                        .content(mapper.writeValueAsString(updateUserRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(dto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(dto.getName())))
                .andExpect(jsonPath("$.email", is(dto.getEmail())));
    }

    @Test
    void createUser() throws Exception {
        NewUserRequestDTO newUserRequest = new NewUserRequestDTO();
        newUserRequest.setEmail("new@mail.com");
        newUserRequest.setName("NewUser");

        dto.setName(newUserRequest.getName());
        dto.setEmail(newUserRequest.getEmail());

        when(service.createUser(any(NewUserRequestDTO.class)))
                .thenReturn(dto);

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(newUserRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(dto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(dto.getName())))
                .andExpect(jsonPath("$.email", is(dto.getEmail())));
    }

    @Test
    void deleteUser() throws Exception {
        mvc.perform(delete("/users/{userId}", dto.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}