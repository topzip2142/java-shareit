package ru.practicum.shareit.request.dto;

import lombok.Data;
import ru.practicum.shareit.item.dto.ItemForRequestDTO;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ItemRequestDTO {
    private Long id;
    private String description;
    private LocalDateTime created;
    private List<ItemForRequestDTO> items;
}