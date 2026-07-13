package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewItemRequest;
import ru.practicum.shareit.item.dto.UpdateItemRequest;

import java.util.Collection;

public interface ItemService {
    ItemDto getItem(long id);

    Collection<ItemDto> getItems(long userId);

    ItemDto createItem(NewItemRequest item, long userId);

    ItemDto updateItem(long itemId, UpdateItemRequest item, long userId);

    Collection<ItemDto> search(long userId, String text);
}