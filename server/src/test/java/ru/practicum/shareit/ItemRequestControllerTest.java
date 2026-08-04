package ru.practicum.shareit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.ItemForRequestDTO;
import ru.practicum.shareit.request.ItemRequestController;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.request.dto.ItemRequestDTO;
import ru.practicum.shareit.request.dto.NewItemRequest;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
    private ObjectMapper mapper;

    @MockBean
    private ItemRequestService service;

    @Autowired
    private MockMvc mvc;

    private final ItemRequestDTO itemRequestDto = new ItemRequestDTO();
    private final ItemForRequestDTO item1 = new ItemForRequestDTO();

    @BeforeEach
    public void beforeEach() {
        itemRequestDto.setId(1L);
        itemRequestDto.setDescription("Тестовое описание");
        itemRequestDto.setCreated(LocalDateTime.now().withNano(0));
        itemRequestDto.setItems(new ArrayList<>());

        item1.setId(1);
        item1.setOwnerId(1);
        item1.setName("Юзер1");
    }

    @Test
    void createItemRequestTest() throws Exception {
        NewItemRequest newItemRequest = new NewItemRequest();
        newItemRequest.setDescription("Описание запроса");

        itemRequestDto.setDescription(newItemRequest.getDescription());

        when(service.createItemRequest(any(), anyLong()))
                .thenReturn(itemRequestDto);

        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(newItemRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription())))
                .andExpect(jsonPath("$.created", is(itemRequestDto.getCreated().toString())))
                .andExpect(jsonPath("$.items").isEmpty());
    }

    @Test
    void getItemRequestTest() throws Exception {
        itemRequestDto.setItems(List.of(item1));

        when(service.getItemRequest(anyLong()))
                .thenReturn(itemRequestDto);

        mvc.perform(get("/requests/{requestId}", itemRequestDto.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription())))
                .andExpect(jsonPath("$.created", is(itemRequestDto.getCreated().toString())))
                .andExpect(jsonPath("$.items", hasSize(1)))
                .andExpect(jsonPath("$.items[0].id", is(1)))
                .andExpect(jsonPath("$.items[0].name", is("Юзер1")))
                .andExpect(jsonPath("$.items[0].ownerId", is(1)));
    }

    @Test
    void getItemRequestsByAuthorIdTest() throws Exception {
        itemRequestDto.setItems(List.of(item1));

        ItemRequestDTO itemRequestDto2 = new ItemRequestDTO();

        itemRequestDto2.setId(2L);
        itemRequestDto2.setDescription("Описание второго запроса");
        itemRequestDto2.setCreated(LocalDateTime.now().minusDays(1).withNano(0));
        itemRequestDto2.setItems(new ArrayList<>());

        List<ItemRequestDTO> itemRequestDtos = List.of(itemRequestDto, itemRequestDto2);

        when(service.getItemRequests(anyLong()))
                .thenReturn(itemRequestDtos);

        mvc.perform(get("/requests")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(itemRequestDto.getDescription())))
                .andExpect(jsonPath("$[0].created", is(itemRequestDto.getCreated().toString())))
                .andExpect(jsonPath("$[0].items", hasSize(1)))
                .andExpect(jsonPath("$[0].items[0].id", is(1)))
                .andExpect(jsonPath("$[0].items[0].name", is("Юзер1")))
                .andExpect(jsonPath("$[0].items[0].ownerId", is(1)))
                .andExpect(jsonPath("$[1].id", is(itemRequestDto2.getId()), Long.class))
                .andExpect(jsonPath("$[1].description", is(itemRequestDto2.getDescription())))
                .andExpect(jsonPath("$[1].created", is(itemRequestDto2.getCreated().toString())))
                .andExpect(jsonPath("$[1].items").isEmpty());
    }

    @Test
    void getAllItemRequestsTest() throws Exception {
        itemRequestDto.setItems(List.of(item1));

        ItemRequestDTO itemRequestDto2 = new ItemRequestDTO();

        itemRequestDto2.setId(2L);
        itemRequestDto2.setDescription("Описание второго запроса");
        itemRequestDto2.setCreated(LocalDateTime.now().minusDays(1).withNano(0));
        itemRequestDto2.setItems(new ArrayList<>());

        List<ItemRequestDTO> itemRequestDtos = List.of(itemRequestDto, itemRequestDto2);

        when(service.getAllItemRequests())
                .thenReturn(itemRequestDtos);

        mvc.perform(get("/requests/all")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(itemRequestDto.getDescription())))
                .andExpect(jsonPath("$[0].created", is(itemRequestDto.getCreated().toString())))
                .andExpect(jsonPath("$[0].items", hasSize(1)))
                .andExpect(jsonPath("$[0].items[0].id", is(1)))
                .andExpect(jsonPath("$[0].items[0].name", is("Юзер1")))
                .andExpect(jsonPath("$[0].items[0].ownerId", is(1)))
                .andExpect(jsonPath("$[1].id", is(itemRequestDto2.getId()), Long.class))
                .andExpect(jsonPath("$[1].description", is(itemRequestDto2.getDescription())))
                .andExpect(jsonPath("$[1].created", is(itemRequestDto2.getCreated().toString())))
                .andExpect(jsonPath("$[1].items").isEmpty());
    }
}