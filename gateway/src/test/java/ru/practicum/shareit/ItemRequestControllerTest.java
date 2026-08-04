package ru.practicum.shareit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.ItemRequestClient;
import ru.practicum.shareit.request.ItemRequestController;
import ru.practicum.shareit.request.dto.NewItemRequestRequest;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
public class ItemRequestControllerTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private ItemRequestClient itemRequestClient;

    @Test
    void createItemRequest() throws Exception {
        NewItemRequestRequest request = new NewItemRequestRequest();
        request.setDescription("Нужна дрель");

        Map<String, Object> requestResponse = Map.of(
                "id", 1,
                "description", "Нужна дрель",
                "created", "2026-07-17T20:30:00",
                "items", List.of()
        );

        when(itemRequestClient.createItemRequest(anyLong(), any(NewItemRequestRequest.class)))
                .thenReturn(ResponseEntity.ok(requestResponse));

        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(request))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.description", is("Нужна дрель")))
                .andExpect(jsonPath("$.created", is("2026-07-17T20:30:00")))
                .andExpect(jsonPath("$.items", hasSize(0)));
    }

    @Test
    void getItemRequest() throws Exception {
        Map<String, Object> requestResponse = Map.of(
                "id", 1,
                "description", "Нужна дрель",
                "items", List.of()
        );

        when(itemRequestClient.getItemRequest(anyLong(), anyLong()))
                .thenReturn(ResponseEntity.ok(requestResponse));

        mvc.perform(get("/requests/{requestId}", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.description", is("Нужна дрель")));
    }

    @Test
    void getItemRequests() throws Exception {
        Map<String, Object> requestResponse = Map.of(
                "id", 1,
                "description", "Нужна дрель"
        );

        when(itemRequestClient.getItemRequests(anyLong()))
                .thenReturn(ResponseEntity.ok(List.of(requestResponse)));

        mvc.perform(get("/requests")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].description", is("Нужна дрель")));
    }

    @Test
    void getAllItemRequests() throws Exception {
        Map<String, Object> requestResponse = Map.of(
                "id", 2,
                "description", "Нужен молоток"
        );

        when(itemRequestClient.getAllItemRequests(anyLong()))
                .thenReturn(ResponseEntity.ok(List.of(requestResponse)));

        mvc.perform(get("/requests/all")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(2)))
                .andExpect(jsonPath("$[0].description", is("Нужен молоток")));
    }
}