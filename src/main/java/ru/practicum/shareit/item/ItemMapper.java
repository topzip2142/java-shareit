package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewItemRequest;
import ru.practicum.shareit.item.dto.UpdateItemRequest;
import ru.practicum.shareit.item.model.Item;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ItemMapper {
    public static ItemDto mapToItemDto(Item item) {
        ItemDto dto = new ItemDto();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setAvailable(item.isAvailable());
        return dto;
    }

    public static Item mapToItem(NewItemRequest request) {
        Item item = new Item();
        item.setDescription(request.getDescription());
        item.setName(request.getName());
        item.setAvailable(request.getAvailable());
        return item;
    }

    public static Item updateFields(Item item, UpdateItemRequest request) {
        if (request.hasDescription()) {
            item.setDescription(request.getDescription());
        }
        if (request.hasName()) {
            item.setName(request.getName());
        }
        if (request.hasAvailable()) {
            item.setAvailable(request.getAvailable());
        }
        return item;
    }
}