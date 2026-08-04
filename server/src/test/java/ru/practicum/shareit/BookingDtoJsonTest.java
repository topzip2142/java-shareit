package ru.practicum.shareit;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dto.BookingResponseDTO;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingDtoJsonTest {
    private final JacksonTester<BookingResponseDTO> json;

    @Test
    void testSerialize() throws Exception {
        LocalDateTime start = LocalDateTime.of(2026, 7, 10, 18, 0, 0);
        LocalDateTime end = LocalDateTime.of(2026, 7, 11, 18, 0, 0);

        User booker = new User();
        booker.setId(2L);
        booker.setName("Юзер");

        Item item = new Item();
        item.setId(3L);
        item.setName("Дрель");

        BookingResponseDTO dto = new BookingResponseDTO();
        dto.setId(1L);
        dto.setStatus(Status.APPROVED);
        dto.setItem(item);
        dto.setBooker(booker);
        dto.setStart(start);
        dto.setEnd(end);

        JsonContent<BookingResponseDTO> result = json.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo("APPROVED");
        assertThat(result).extractingJsonPathNumberValue("$.item.id").isEqualTo(3);
        assertThat(result).extractingJsonPathNumberValue("$.booker.id").isEqualTo(2);
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo("2026-07-10T18:00:00");
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo("2026-07-11T18:00:00");
    }
}