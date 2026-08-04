package ru.practicum.shareit;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.ItemForRequestDTO;
import ru.practicum.shareit.request.dto.ItemRequestDTO;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestDtoJsonTest {
    private final JacksonTester<ItemRequestDTO> json;

    @Test
    void testSerialize() throws Exception {
        LocalDateTime now = LocalDateTime.of(2026, 7, 10, 18, 5, 26);
        List<ItemForRequestDTO> items = new ArrayList<>();

        ItemRequestDTO dto = new ItemRequestDTO();
        dto.setId(1L);
        dto.setDescription("Описание запроса");
        dto.setCreated(now);
        dto.setItems(items);

        JsonContent<ItemRequestDTO> result = json.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("Описание запроса");
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo("2026-07-10T18:05:26");
        assertThat(result).extractingJsonPathArrayValue("$.items").isEmpty();
    }
}