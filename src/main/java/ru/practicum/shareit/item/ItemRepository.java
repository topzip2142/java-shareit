package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.Optional;

public interface ItemRepository {
    Optional<Item> getItem(long id);

    Collection<Item> getItems(long userId);

    Item createItem(Item item, long userId);

    Item updateItem(Item item);

    void delItem(long id);
}