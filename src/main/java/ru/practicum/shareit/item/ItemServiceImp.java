package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewItemRequest;
import ru.practicum.shareit.item.dto.UpdateItemRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImp implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public ItemDto getItem(long id) {
        return itemRepository.getItem(id)
                .map(ItemMapper::mapToItemDto)
                .orElseThrow(() -> new NotFoundException("Вещь с ID: " + id + " не найдена"));
    }

    @Override
    public Collection<ItemDto> getItems(long userId) {
        if (!isUserExist(userId)) {
            throw new NotFoundException("Пользователь с ID: " + userId + " не найден");
        }
        return itemRepository.getItems(userId)
                .stream()
                .map(ItemMapper::mapToItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto createItem(NewItemRequest request, long userId) {
        if (!isUserExist(userId)) {
            throw new NotFoundException("Пользователь с ID: " + userId + " не найден");
        }
        if (request.getName() == null || request.getName().isEmpty()) {
            throw new ValidationException("Имя должно быть указано");
        }
        if (request.getDescription() == null || request.getDescription().isEmpty()) {
            throw new ValidationException("Описание должно быть указано");
        }
        if (request.getAvailable() == null) {
            throw new ValidationException("Доступность должна быть указана");
        }
        Item item = ItemMapper.mapToItem(request);
        item = itemRepository.createItem(item, userId);
        return ItemMapper.mapToItemDto(item);
    }

    @Override
    public ItemDto updateItem(long itemId, UpdateItemRequest itemFields, long userId) {
        if (!isUserExist(userId)) {
            throw new NotFoundException("Пользователь с ID: " + userId + " не найден");
        }
        Item updatedItem = itemRepository.getItem(itemId)
                .map(item -> ItemMapper.updateFields(item, itemFields))
                .orElseThrow(() -> new NotFoundException("Вещь с ID: " + itemId + " не найдена"));
        return ItemMapper.mapToItemDto(itemRepository.updateItem(updatedItem));
    }

    @Override
    public Collection<ItemDto> search(long userId, String text) {
        if (!isUserExist(userId)) {
            throw new NotFoundException("Пользователь с ID: " + userId + " не найден");
        }
        String lowerText = text.toLowerCase().trim();
        return itemRepository.getItems(userId).stream()
                .filter(Item::isAvailable)
                .filter(item ->
                        item.getName().toLowerCase().contains(lowerText)
                                || item.getDescription().toLowerCase().contains(lowerText)
                )
                .map(ItemMapper::mapToItemDto)
                .toList();
    }

    private boolean isUserExist(long id) {
        return userRepository.getUser(id).isPresent();
    }
}