package ru.practicum.shareit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dto.BookingResponseDTO;
import ru.practicum.shareit.booking.dto.BookingRequestDTO;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(controllers = BookingController.class)
public class BookingControllerTest {

    @Autowired
    ObjectMapper mapper;

    @MockBean
    BookingService service;

    @Autowired
    private MockMvc mvc;

    private BookingResponseDTO bookingDto = new BookingResponseDTO();
    private static Item item = new Item();
    private static User booker = new User();
    private static User owner = new User();

    @BeforeAll
    public static void beforeAll() {
        owner.setId(1L);
        owner.setName("Хозяин");
        owner.setEmail("owner@mail.com");

        booker.setId(2L);
        booker.setName("Букер");
        booker.setEmail("booker@mail.com");

        item.setId(1L);
        item.setOwner(owner);
        item.setDescription("описание вещи");
        item.setName("Вещь");
        item.setAvailable(true);
    }

    @BeforeEach
    public void beforeEach() {
        bookingDto.setId(1L);
        bookingDto.setItem(item);
        bookingDto.setBooker(booker);
    }

    @Test
    void createBooking() throws Exception {
        BookingRequestDTO newBookingRequest = new BookingRequestDTO();
        newBookingRequest.setItemId(item.getId());
        newBookingRequest.setStart(LocalDateTime.now().plusDays(1).withNano(0));
        newBookingRequest.setEnd(LocalDateTime.now().plusDays(3).withNano(0));

        bookingDto.setStatus(Status.WAITING);
        bookingDto.setStart(newBookingRequest.getStart());
        bookingDto.setEnd(newBookingRequest.getEnd());

        when(service.createBooking(any(), anyLong()))
                .thenReturn(bookingDto);

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(newBookingRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus().toString())))
                .andExpect(jsonPath("$.item.id", is(bookingDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.item.name", is(bookingDto.getItem().getName())))
                .andExpect(jsonPath("$.booker.id", is(bookingDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.start", is(bookingDto.getStart().toString())))
                .andExpect(jsonPath("$.end", is(bookingDto.getEnd().toString())));
    }

    @Test
    void approveBooking() throws Exception {
        bookingDto.setStatus(Status.APPROVED);
        bookingDto.setStart(LocalDateTime.now().plusDays(1).withNano(0));
        bookingDto.setEnd(LocalDateTime.now().plusDays(3).withNano(0));

        when(service.approveBooking(anyLong(), anyLong(), anyBoolean()))
                .thenReturn(bookingDto);

        mvc.perform(patch("/bookings/{bookingId}", bookingDto.getId())
                        .queryParam("approved", "true")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus().toString())))
                .andExpect(jsonPath("$.item.id", is(bookingDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.item.name", is(bookingDto.getItem().getName())))
                .andExpect(jsonPath("$.booker.id", is(bookingDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.start", is(bookingDto.getStart().toString())))
                .andExpect(jsonPath("$.end", is(bookingDto.getEnd().toString())));
    }

    @Test
    void getBooking() throws Exception {
        bookingDto.setStatus(Status.APPROVED);
        bookingDto.setStart(LocalDateTime.now().plusDays(1).withNano(0));
        bookingDto.setEnd(LocalDateTime.now().plusDays(3).withNano(0));

        when(service.getBooking(anyLong(), anyLong()))
                .thenReturn(bookingDto);

        mvc.perform(get("/bookings/{bookingId}", bookingDto.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus().toString())))
                .andExpect(jsonPath("$.item.id", is(bookingDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.item.name", is(bookingDto.getItem().getName())))
                .andExpect(jsonPath("$.booker.id", is(bookingDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.start", is(bookingDto.getStart().toString())))
                .andExpect(jsonPath("$.end", is(bookingDto.getEnd().toString())));
    }

    @Test
    void getAllBookings() throws Exception {
        bookingDto.setStatus(Status.APPROVED);
        bookingDto.setStart(LocalDateTime.now().plusDays(1).withNano(0));
        bookingDto.setEnd(LocalDateTime.now().plusDays(3).withNano(0));
        List<BookingResponseDTO> bookingDtos = List.of(bookingDto);

        when(service.getUserBookings(anyLong(), anyString()))
                .thenReturn(bookingDtos);

        mvc.perform(get("/bookings")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(bookingDtos.get(0).getId()), Long.class))
                .andExpect(jsonPath("$[0].status", is(bookingDtos.get(0).getStatus().toString())))
                .andExpect(jsonPath("$[0].item.id", is(bookingDtos.get(0).getItem().getId()), Long.class))
                .andExpect(jsonPath("$[0].item.name", is(bookingDtos.get(0).getItem().getName())))
                .andExpect(jsonPath("$[0].booker.id", is(bookingDtos.get(0).getBooker().getId()), Long.class))
                .andExpect(jsonPath("$[0].start", is(bookingDtos.get(0).getStart().toString())))
                .andExpect(jsonPath("$[0].end", is(bookingDtos.get(0).getEnd().toString())));
    }

    @Test
    void getAllBookingsByOwner() throws Exception {
        bookingDto.setStatus(Status.APPROVED);
        bookingDto.setStart(LocalDateTime.now().plusDays(1).withNano(0));
        bookingDto.setEnd(LocalDateTime.now().plusDays(3).withNano(0));
        List<BookingResponseDTO> bookingDtos = List.of(bookingDto);

        when(service.getOwnerBookings(anyLong(), anyString()))
                .thenReturn(bookingDtos);

        mvc.perform(get("/bookings/owner")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(bookingDtos.get(0).getId()), Long.class))
                .andExpect(jsonPath("$[0].status", is(bookingDtos.get(0).getStatus().toString())))
                .andExpect(jsonPath("$[0].item.id", is(bookingDtos.get(0).getItem().getId()), Long.class))
                .andExpect(jsonPath("$[0].item.name", is(bookingDtos.get(0).getItem().getName())))
                .andExpect(jsonPath("$[0].booker.id", is(bookingDtos.get(0).getBooker().getId()), Long.class))
                .andExpect(jsonPath("$[0].start", is(bookingDtos.get(0).getStart().toString())))
                .andExpect(jsonPath("$[0].end", is(bookingDtos.get(0).getEnd().toString())));
    }

}