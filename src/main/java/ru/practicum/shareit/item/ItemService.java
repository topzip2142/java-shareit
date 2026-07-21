package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewItemRequest;
import ru.practicum.shareit.item.dto.UpdateItemRequest;
import ru.practicum.shareit.item.model.Comment;

import java.util.Collection;

public interface ItemService {
    ItemBookingDto getItem(long id);

    Collection<ItemBookingDto> getItems(long userId);

    ItemDto createItem(NewItemRequest item, long userId);

    ItemDto updateItem(long itemId, UpdateItemRequest item, long userId);

    Collection<ItemDto> search(long userId, String text);

    CommentDto addComment(long authorId, long itemId, Comment comment);
}