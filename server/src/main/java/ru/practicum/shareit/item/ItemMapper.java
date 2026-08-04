package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ItemMapper {
    public static ItemResponseDTO mapToItemDto(Item item) {
        ItemResponseDTO dto = new ItemResponseDTO();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setAvailable(item.isAvailable());
        return dto;
    }

    public static Item mapToItem(ItemRequestDTO request) {
        Item item = new Item();
        item.setDescription(request.getDescription());
        item.setName(request.getName());
        item.setAvailable(request.getAvailable());
        return item;
    }

    public static ItemBookingResponseDTO mapToItemBookingDto(Item item, Booking last, Booking next) {
        ItemBookingResponseDTO dto = new ItemBookingResponseDTO();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setAvailable(item.isAvailable());
        dto.setLastBooking(last);
        dto.setNextBooking(next);
        return dto;
    }

    public static ItemWithCommentsResponseDTO mapToItemWithCommentsDto(Item item, List<CommentResponseDTO> comments) {
        ItemWithCommentsResponseDTO dto = new ItemWithCommentsResponseDTO();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setAvailable(item.isAvailable());
        dto.setComments(comments);
        return dto;
    }

    public static ItemForRequestDTO mapToItemForRequestDto(Item item) {
        ItemForRequestDTO dto = new ItemForRequestDTO();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setOwnerId(item.getOwner().getId());
        return dto;
    }

    public static Item updateFields(Item item, UpdateItemRequestDTO request) {
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