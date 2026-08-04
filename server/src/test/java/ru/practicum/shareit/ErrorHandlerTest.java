package ru.practicum.shareit;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.DuplicatedDataException;
import ru.practicum.shareit.exception.ErrorHandler;
import ru.practicum.shareit.exception.NoAccessException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.ItemServiceImp;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.UserServiceImp;
import ru.practicum.shareit.user.dto.NewUserRequestDTO;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({UserController.class, ItemController.class})
@Import(ErrorHandler.class)
class ErrorHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper mapper;

    @MockBean
    private UserServiceImp userService;

    @MockBean
    private ItemServiceImp itemService;

    @Test
    void handleNotFound() throws Exception {
        when(userService.getUser(anyLong()))
                .thenThrow(new NotFoundException("Пользователь с id 999 не найден"));

        mockMvc.perform(get("/users/{userId}", 999L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is("Пользователь с id 999 не найден")));
    }

    @Test
    void accessDenied() throws Exception {
        when(itemService.updateItem(anyLong(), any(), anyLong()))
                .thenThrow(new NoAccessException("Пользователь не является владельцем вещи"));

        mockMvc.perform(patch("/items/{itemId}", 1L)
                        .header("X-Sharer-User-Id", 2L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"New Name\"}")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error", is("Пользователь не является владельцем вещи")));
    }

    @Test
    void handleDuplicatedData() throws Exception {
        when(userService.createUser(any()))
                .thenThrow(new DuplicatedDataException("Email уже занят"));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Alex\",\"email\":\"v@mail.com\"}")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error", is("Email уже занят")));
    }

    @Test
    void handleValidation() throws Exception {
        NewUserRequestDTO userRequest = new NewUserRequestDTO();
        when(userService.createUser(any()))
                .thenThrow(new ValidationException("Email не может быть пустым"));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userRequest))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error", is("Email не может быть пустым")));
    }

    @Test
    void handleOther() throws Exception {
        when(userService.getUser(anyLong()))
                .thenThrow(new RuntimeException("Ошибка подключения к базе данных"));

        mockMvc.perform(get("/users/{userId}", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error", is("Ошибка подключения к базе данных")));
    }
}