package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentResponseDTO;
import ru.practicum.shareit.item.dto.ItemBookingResponseDTO;
import ru.practicum.shareit.item.dto.ItemResponseDTO;
import ru.practicum.shareit.item.dto.ItemRequestDTO;
import ru.practicum.shareit.item.dto.UpdateItemRequestDTO;
import ru.practicum.shareit.item.model.Comment;

import java.util.Collection;

public interface ItemService {
    ItemBookingResponseDTO getItem(long id);

    Collection<ItemBookingResponseDTO> getItems(long userId);

    ItemResponseDTO createItem(ItemRequestDTO item, long userId);

    ItemResponseDTO updateItem(long itemId, UpdateItemRequestDTO item, long userId);

    Collection<ItemResponseDTO> search(long userId, String text);

    CommentResponseDTO addComment(long authorId, long itemId, Comment comment);
}