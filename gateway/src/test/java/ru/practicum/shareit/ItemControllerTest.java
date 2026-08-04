package ru.practicum.shareit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.ItemClient;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.dto.NewItemRequest;
import ru.practicum.shareit.item.dto.UpdateItemRequest;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
public class ItemControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private ItemClient itemClient;

    @Test
    void getItems() throws Exception {
        Map<String, Object> itemResponse = Map.of(
                "id", 1,
                "name", "Дрель",
                "description", "Ударная дрель",
                "available", true
        );

        when(itemClient.getItems(anyLong()))
                .thenReturn(ResponseEntity.ok(List.of(itemResponse)));

        mvc.perform(get("/items")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("Дрель")))
                .andExpect(jsonPath("$[0].description", is("Ударная дрель")))
                .andExpect(jsonPath("$[0].available", is(true)));
    }

    @Test
    void getItem() throws Exception {
        Map<String, Object> itemResponse = Map.of(
                "id", 42,
                "name", "Отвертка",
                "description", "Аккумуляторная",
                "available", true
        );

        when(itemClient.getItem(anyLong(), anyLong()))
                .thenReturn(ResponseEntity.ok(itemResponse));

        mvc.perform(get("/items/{itemId}", 42L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(42)))
                .andExpect(jsonPath("$.name", is("Отвертка")))
                .andExpect(jsonPath("$.description", is("Аккумуляторная")))
                .andExpect(jsonPath("$.available", is(true)));
    }

    @Test
    void createItem() throws Exception {
        NewItemRequest request = new NewItemRequest();
        request.setName("Молоток");
        request.setDescription("Стальной");
        request.setAvailable(true);

        Map<String, Object> itemResponse = Map.of(
                "id", 2,
                "name", "Молоток",
                "description", "Стальной",
                "available", true
        );

        when(itemClient.createItem(anyLong(), any(NewItemRequest.class)))
                .thenReturn(ResponseEntity.ok(itemResponse));

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(request))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(2)))
                .andExpect(jsonPath("$.name", is("Молоток")))
                .andExpect(jsonPath("$.description", is("Стальной")))
                .andExpect(jsonPath("$.available", is(true)));
    }

    @Test
    void updateItem() throws Exception {
        UpdateItemRequest request = new UpdateItemRequest();
        request.setName("Новый молоток");

        Map<String, Object> itemResponse = Map.of(
                "id", 2,
                "name", "Новый молоток",
                "description", "Стальной",
                "available", true
        );

        when(itemClient.updateItem(anyLong(), anyLong(), any(UpdateItemRequest.class)))
                .thenReturn(ResponseEntity.ok(itemResponse));

        mvc.perform(patch("/items/{itemId}", 2L)
                        .content(mapper.writeValueAsString(request))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(2)))
                .andExpect(jsonPath("$.name", is("Новый молоток")))
                .andExpect(jsonPath("$.description", is("Стальной")));
    }

    @Test
    void searchItem() throws Exception {
        Map<String, Object> itemResponse = Map.of(
                "id", 3,
                "name", "Пила",
                "description", "Циркулярная пила",
                "available", true
        );

        when(itemClient.searchItems(anyLong(), anyString()))
                .thenReturn(ResponseEntity.ok(List.of(itemResponse)));

        mvc.perform(get("/items/search")
                        .queryParam("text", "пила")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(3)))
                .andExpect(jsonPath("$[0].name", is("Пила")))
                .andExpect(jsonPath("$[0].description", is("Циркулярная пила")));
    }

    @Test
    void addComment() throws Exception {
        Map<String, String> commentBody = Map.of("text", "Все супер!");
        Map<String, Object> commentResponse = Map.of(
                "id", 10,
                "text", "Все супер!",
                "authorName", "Иван"
        );

        when(itemClient.addComment(anyLong(), anyLong(), any()))
                .thenReturn(ResponseEntity.ok(commentResponse));

        mvc.perform(post("/items/{itemId}/comment", 1L)
                        .content(mapper.writeValueAsString(commentBody))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(10)))
                .andExpect(jsonPath("$.text", is("Все супер!")))
                .andExpect(jsonPath("$.authorName", is("Иван")));
    }
}