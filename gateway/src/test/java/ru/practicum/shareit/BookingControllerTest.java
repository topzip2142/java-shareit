package ru.practicum.shareit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.BookingClient;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.booking.dto.NewBookingRequest;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
public class BookingControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private BookingClient bookingClient;

    @Test
    void createBooking() throws Exception {
        NewBookingRequest request = new NewBookingRequest();
        request.setItemId(1L);
        request.setStart(LocalDateTime.of(2026, 7, 20, 10, 0));
        request.setEnd(LocalDateTime.of(2026, 7, 21, 10, 0));

        Map<String, Object> bookingResponse = Map.of(
                "id", 1,
                "status", "WAITING",
                "start", "2026-07-20T10:00:00",
                "end", "2026-07-21T10:00:00",
                "item", Map.of("id", 1, "name", "Дрель"),
                "booker", Map.of("id", 2, "name", "Юзер")
        );

        when(bookingClient.createBooking(any(NewBookingRequest.class), anyLong()))
                .thenReturn(ResponseEntity.ok(bookingResponse));

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(request))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 2L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.status", is("WAITING")))
                .andExpect(jsonPath("$.start", is("2026-07-20T10:00:00")))
                .andExpect(jsonPath("$.end", is("2026-07-21T10:00:00")))
                .andExpect(jsonPath("$.item.id", is(1)))
                .andExpect(jsonPath("$.booker.id", is(2)));
    }

    @Test
    void approveBooking() throws Exception {
        Map<String, Object> bookingResponse = Map.of(
                "id", 1,
                "status", "APPROVED"
        );

        when(bookingClient.approveBooking(anyLong(), anyLong(), anyBoolean()))
                .thenReturn(ResponseEntity.ok(bookingResponse));

        mvc.perform(patch("/bookings/{bookingId}", 1L)
                        .queryParam("approved", "true")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.status", is("APPROVED")));
    }

    @Test
    void getBooking() throws Exception {
        Map<String, Object> bookingResponse = Map.of(
                "id", 1,
                "status", "APPROVED"
        );

        when(bookingClient.getBooking(anyLong(), anyLong()))
                .thenReturn(ResponseEntity.ok(bookingResponse));

        mvc.perform(get("/bookings/{bookingId}", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.status", is("APPROVED")));
    }

    @Test
    void getAllBookings() throws Exception {
        Map<String, Object> bookingResponse = Map.of(
                "id", 1,
                "status", "WAITING"
        );

        when(bookingClient.getAllBookings(anyLong(), anyString()))
                .thenReturn(ResponseEntity.ok(List.of(bookingResponse)));

        mvc.perform(get("/bookings")
                        .queryParam("state", "WAITING")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].status", is("WAITING")));
    }

    @Test
    void getAllBookingsByOwner() throws Exception {
        Map<String, Object> bookingResponse = Map.of(
                "id", 1,
                "status", "APPROVED"
        );

        when(bookingClient.getOwnerBookings(anyLong(), anyString()))
                .thenReturn(ResponseEntity.ok(List.of(bookingResponse)));

        mvc.perform(get("/bookings/owner")
                        .queryParam("state", "ALL")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].status", is("APPROVED")));
    }
}