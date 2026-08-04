package ru.practicum.shareit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.CommentResponseDTO;
import ru.practicum.shareit.item.dto.ItemBookingResponseDTO;
import ru.practicum.shareit.item.dto.ItemResponseDTO;
import ru.practicum.shareit.item.dto.ItemRequestDTO;
import ru.practicum.shareit.item.dto.UpdateItemRequestDTO;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
public class ItemControllerTest {
    @Autowired
    ObjectMapper mapper;

    @MockBean
    ItemService service;

    @Autowired
    private MockMvc mvc;
    private static Item item = new Item();
    private static User owner = new User();
    private static User booker1 = new User();
    private static User booker2 = new User();

    @Test
    void createItem() throws Exception {
        ItemRequestDTO newItemRequest = new ItemRequestDTO();
        newItemRequest.setAvailable(false);
        newItemRequest.setDescription("Описание ");
        newItemRequest.setName("Название");

        ItemResponseDTO dto = new ItemResponseDTO();
        dto.setAvailable(newItemRequest.getAvailable());
        dto.setName(newItemRequest.getName());
        dto.setDescription(newItemRequest.getDescription());
        dto.setId(1L);

        when(service.createItem(any(ItemRequestDTO.class), anyLong()))
                .thenReturn(dto);

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(newItemRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(dto.getId()), Long.class))
                .andExpect(jsonPath("$.available", is(dto.isAvailable())))
                .andExpect(jsonPath("$.name", is(dto.getName())))
                .andExpect(jsonPath("$.description", is(dto.getDescription())));
    }

    @Test
    void getItem() throws Exception {
        booker1.setId(1L);
        booker1.setName("Букер1");
        booker1.setEmail("booker@mail.com");

        booker2.setId(2L);
        booker2.setName("Букер2");
        booker2.setEmail("booker2@mail.com");

        owner.setId(3L);
        owner.setName("Хозяин");
        owner.setEmail("owner@mail.com");

        item.setId(1L);
        item.setOwner(owner);
        item.setDescription("описание вещи");
        item.setName("Вещь");
        item.setAvailable(true);

        CommentResponseDTO comment = new CommentResponseDTO();
        comment.setId(1L);
        comment.setText("отзыв");
        comment.setCreated(LocalDateTime.now().minusDays(1).withNano(0));
        comment.setAuthorName("АвторОтзыва");

        Booking lastBook = new Booking();
        lastBook.setId(1L);
        lastBook.setItem(item);
        lastBook.setBooker(booker1);
        lastBook.setEndDate(LocalDateTime.now().minusDays(3).withNano(0));
        lastBook.setStartDate(LocalDateTime.now().minusDays(4).withNano(0));
        lastBook.setStatus(Status.APPROVED);

        Booking nextBook = new Booking();
        nextBook.setId(2L);
        nextBook.setItem(item);
        nextBook.setBooker(booker2);
        nextBook.setEndDate(LocalDateTime.now().plusDays(2).withNano(0));
        nextBook.setStartDate(LocalDateTime.now().plusDays(1).withNano(0));
        nextBook.setStatus(Status.WAITING);

        ItemBookingResponseDTO dto = new ItemBookingResponseDTO();
        dto.setId(1L);
        dto.setAvailable(false);
        dto.setName("Вещь");
        dto.setDescription("Описание");
        dto.setLastBooking(lastBook);
        dto.setNextBooking(nextBook);
        dto.setComments(List.of(comment));

        when(service.getItem(anyLong()))
                .thenReturn(dto);

        mvc.perform(get("/items/{itemId}", item.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(dto.getId()), Long.class))
                .andExpect(jsonPath("$.available", is(dto.isAvailable())))
                .andExpect(jsonPath("$.name", is(dto.getName())))
                .andExpect(jsonPath("$.description", is(dto.getDescription())))

                .andExpect(jsonPath("$.lastBooking.id", is(dto.getLastBooking().getId()), Long.class))
                .andExpect(jsonPath("$.lastBooking.item.id", is(dto.getLastBooking().getItem().getId()), Long.class))
                .andExpect(jsonPath("$.lastBooking.item.owner.id", is(dto.getLastBooking().getItem().getOwner().getId()), Long.class))
                .andExpect(jsonPath("$.lastBooking.item.owner.name", is(dto.getLastBooking().getItem().getOwner().getName())))
                .andExpect(jsonPath("$.lastBooking.item.owner.email", is(dto.getLastBooking().getItem().getOwner().getEmail())))
                .andExpect(jsonPath("$.lastBooking.booker.id", is(dto.getLastBooking().getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.lastBooking.booker.name", is(dto.getLastBooking().getBooker().getName())))
                .andExpect(jsonPath("$.lastBooking.booker.email", is(dto.getLastBooking().getBooker().getEmail())))
                .andExpect(jsonPath("$.lastBooking.startDate", is(dto.getLastBooking().getStartDate().toString())))
                .andExpect(jsonPath("$.lastBooking.endDate", is(dto.getLastBooking().getEndDate().toString())))

                .andExpect(jsonPath("$.nextBooking.id", is(dto.getNextBooking().getId()), Long.class))
                .andExpect(jsonPath("$.nextBooking.item.id", is(dto.getNextBooking().getItem().getId()), Long.class))
                .andExpect(jsonPath("$.nextBooking.item.owner.id", is(dto.getNextBooking().getItem().getOwner().getId()), Long.class))
                .andExpect(jsonPath("$.nextBooking.item.owner.name", is(dto.getNextBooking().getItem().getOwner().getName())))
                .andExpect(jsonPath("$.nextBooking.item.owner.email", is(dto.getNextBooking().getItem().getOwner().getEmail())))
                .andExpect(jsonPath("$.nextBooking.booker.id", is(dto.getNextBooking().getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.nextBooking.booker.name", is(dto.getNextBooking().getBooker().getName())))
                .andExpect(jsonPath("$.nextBooking.booker.email", is(dto.getNextBooking().getBooker().getEmail())))
                .andExpect(jsonPath("$.nextBooking.startDate", is(dto.getNextBooking().getStartDate().toString())))
                .andExpect(jsonPath("$.nextBooking.endDate", is(dto.getNextBooking().getEndDate().toString())))

                .andExpect(jsonPath("$.comments[0].id", is(dto.getComments().get(0).getId()), Long.class))
                .andExpect(jsonPath("$.comments[0].authorName", is(dto.getComments().get(0).getAuthorName())))
                .andExpect(jsonPath("$.comments[0].text", is(dto.getComments().get(0).getText())))
                .andExpect(jsonPath("$.comments[0].created", is(dto.getComments().get(0).getCreated().toString())));


    }

    @Test
    void getItems() throws Exception {
        booker1.setId(1L);
        booker1.setName("Букер1");
        booker1.setEmail("booker@mail.com");

        booker2.setId(2L);
        booker2.setName("Букер2");
        booker2.setEmail("booker2@mail.com");

        owner.setId(3L);
        owner.setName("Хозяин");
        owner.setEmail("owner@mail.com");

        item.setId(1L);
        item.setOwner(owner);
        item.setDescription("описание вещи");
        item.setName("Вещь");
        item.setAvailable(true);

        CommentResponseDTO comment = new CommentResponseDTO();
        comment.setId(1L);
        comment.setText("отзыв");
        comment.setCreated(LocalDateTime.now().minusDays(1).withNano(0));
        comment.setAuthorName("АвторОтзыва");

        Booking lastBook = new Booking();
        lastBook.setId(1L);
        lastBook.setItem(item);
        lastBook.setBooker(booker1);
        lastBook.setEndDate(LocalDateTime.now().minusDays(3).withNano(0));
        lastBook.setStartDate(LocalDateTime.now().minusDays(4).withNano(0));
        lastBook.setStatus(Status.APPROVED);

        Booking nextBook = new Booking();
        nextBook.setId(2L);
        nextBook.setItem(item);
        nextBook.setBooker(booker2);
        nextBook.setEndDate(LocalDateTime.now().plusDays(2).withNano(0));
        nextBook.setStartDate(LocalDateTime.now().plusDays(1).withNano(0));
        nextBook.setStatus(Status.WAITING);

        ItemBookingResponseDTO dto = new ItemBookingResponseDTO();
        dto.setId(1L);
        dto.setAvailable(false);
        dto.setName("Вещь");
        dto.setDescription("Описание");
        dto.setLastBooking(lastBook);
        dto.setNextBooking(nextBook);
        dto.setComments(List.of(comment));

        ItemBookingResponseDTO dto2 = new ItemBookingResponseDTO();
        dto2.setId(2L);
        dto2.setAvailable(false);
        dto2.setName("Вещь2");
        dto2.setDescription("Описание2");

        when(service.getItems(anyLong()))
                .thenReturn(List.of(dto, dto2));

        mvc.perform(get("/items")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(dto.getId()), Long.class))
                .andExpect(jsonPath("$[0].available", is(dto.isAvailable())))
                .andExpect(jsonPath("$[0].name", is(dto.getName())))
                .andExpect(jsonPath("$[0].description", is(dto.getDescription())))

                .andExpect(jsonPath("$[0].lastBooking.id", is(dto.getLastBooking().getId()), Long.class))
                .andExpect(jsonPath("$[0].lastBooking.item.id", is(dto.getLastBooking().getItem().getId()), Long.class))
                .andExpect(jsonPath("$[0].lastBooking.item.owner.id", is(dto.getLastBooking().getItem().getOwner().getId()), Long.class))
                .andExpect(jsonPath("$[0].lastBooking.item.owner.name", is(dto.getLastBooking().getItem().getOwner().getName())))
                .andExpect(jsonPath("$[0].lastBooking.item.owner.email", is(dto.getLastBooking().getItem().getOwner().getEmail())))
                .andExpect(jsonPath("$[0].lastBooking.booker.id", is(dto.getLastBooking().getBooker().getId()), Long.class))
                .andExpect(jsonPath("$[0].lastBooking.booker.name", is(dto.getLastBooking().getBooker().getName())))
                .andExpect(jsonPath("$[0].lastBooking.booker.email", is(dto.getLastBooking().getBooker().getEmail())))
                .andExpect(jsonPath("$[0].lastBooking.startDate", is(dto.getLastBooking().getStartDate().toString())))
                .andExpect(jsonPath("$[0].lastBooking.endDate", is(dto.getLastBooking().getEndDate().toString())))

                .andExpect(jsonPath("$[0].nextBooking.id", is(dto.getNextBooking().getId()), Long.class))
                .andExpect(jsonPath("$[0].nextBooking.item.id", is(dto.getNextBooking().getItem().getId()), Long.class))
                .andExpect(jsonPath("$[0].nextBooking.item.owner.id", is(dto.getNextBooking().getItem().getOwner().getId()), Long.class))
                .andExpect(jsonPath("$[0].nextBooking.item.owner.name", is(dto.getNextBooking().getItem().getOwner().getName())))
                .andExpect(jsonPath("$[0].nextBooking.item.owner.email", is(dto.getNextBooking().getItem().getOwner().getEmail())))
                .andExpect(jsonPath("$[0].nextBooking.booker.id", is(dto.getNextBooking().getBooker().getId()), Long.class))
                .andExpect(jsonPath("$[0].nextBooking.booker.name", is(dto.getNextBooking().getBooker().getName())))
                .andExpect(jsonPath("$[0].nextBooking.booker.email", is(dto.getNextBooking().getBooker().getEmail())))
                .andExpect(jsonPath("$[0].nextBooking.startDate", is(dto.getNextBooking().getStartDate().toString())))
                .andExpect(jsonPath("$[0].nextBooking.endDate", is(dto.getNextBooking().getEndDate().toString())))

                .andExpect(jsonPath("$[0].comments[0].id", is(dto.getComments().get(0).getId()), Long.class))
                .andExpect(jsonPath("$[0].comments[0].authorName", is(dto.getComments().get(0).getAuthorName())))
                .andExpect(jsonPath("$[0].comments[0].text", is(dto.getComments().get(0).getText())))
                .andExpect(jsonPath("$[0].comments[0].created", is(dto.getComments().get(0).getCreated().toString())))

                .andExpect(jsonPath("$[1].id", is(dto2.getId()), Long.class))
                .andExpect(jsonPath("$[1].available", is(dto2.isAvailable())))
                .andExpect(jsonPath("$[1].name", is(dto2.getName())))
                .andExpect(jsonPath("$[1].description", is(dto2.getDescription())))
                .andExpect(jsonPath("$[1].lastBooking", is(dto2.getLastBooking())))
                .andExpect(jsonPath("$[1].nextBooking", is(dto2.getNextBooking())))
                .andExpect(jsonPath("$[1].comments", is(dto2.getComments())));


    }

    @Test
    void updateItem() throws Exception {
        UpdateItemRequestDTO updateItemRequest = new UpdateItemRequestDTO();
        updateItemRequest.setAvailable(true);
        updateItemRequest.setDescription("Новое описание");
        updateItemRequest.setName("Новое название");

        ItemResponseDTO dto = new ItemResponseDTO();
        dto.setId(2L);
        dto.setAvailable(updateItemRequest.getAvailable());
        dto.setName(updateItemRequest.getName());
        dto.setDescription(updateItemRequest.getDescription());

        when(service.updateItem(anyLong(), eq(updateItemRequest), anyLong()))
                .thenReturn(dto);

        mvc.perform(patch("/items/{itemId}", dto.getId())
                        .content(mapper.writeValueAsString(updateItemRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(dto.getId()), Long.class))
                .andExpect(jsonPath("$.available", is(dto.isAvailable())))
                .andExpect(jsonPath("$.name", is(dto.getName())))
                .andExpect(jsonPath("$.description", is(dto.getDescription())));
    }

    @Test
    void search() throws Exception {
        ItemResponseDTO dto = new ItemResponseDTO();
        dto.setId(1L);
        dto.setAvailable(false);
        dto.setName("Вещь1");
        dto.setDescription("Описание1");

        ItemResponseDTO dto2 = new ItemResponseDTO();
        dto2.setId(2L);
        dto2.setAvailable(true);
        dto2.setName("Вещь2");
        dto2.setDescription("Описание2");

        when(service.search(anyLong(), anyString()))
                .thenReturn(List.of(dto, dto2));

        mvc.perform(get("/items/search")
                        .queryParam("text", "поиск")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(dto.getId()), Long.class))
                .andExpect(jsonPath("$[0].available", is(dto.isAvailable())))
                .andExpect(jsonPath("$[0].name", is(dto.getName())))
                .andExpect(jsonPath("$[0].description", is(dto.getDescription())))

                .andExpect(jsonPath("$[1].id", is(dto2.getId()), Long.class))
                .andExpect(jsonPath("$[1].available", is(dto2.isAvailable())))
                .andExpect(jsonPath("$[1].name", is(dto2.getName())))
                .andExpect(jsonPath("$[1].description", is(dto2.getDescription())));
    }

    @Test
    void addComment() throws Exception {
        Comment comment = new Comment();
        comment.setText("Текст отзыва");

        CommentResponseDTO commentDto = new CommentResponseDTO();
        commentDto.setAuthorName("АвторОтзыва");
        commentDto.setId(1L);
        commentDto.setCreated(LocalDateTime.now().withNano(0));
        commentDto.setText(comment.getText());

        when(service.addComment(anyLong(), anyLong(), any(Comment.class)))
                .thenReturn(commentDto);

        mvc.perform(post("/items/{itemId}/comment", 1L)
                        .content(mapper.writeValueAsString(comment))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(commentDto.getId()), Long.class))
                .andExpect(jsonPath("$.authorName", is(commentDto.getAuthorName())))
                .andExpect(jsonPath("$.created", is(commentDto.getCreated().toString())))
                .andExpect(jsonPath("$.text", is(commentDto.getText())));
    }
}