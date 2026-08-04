package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemForRequestDTO;
import ru.practicum.shareit.request.dto.ItemRequestDTO;
import ru.practicum.shareit.request.dto.NewItemRequest;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImp implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public ItemRequestDTO createItemRequest(NewItemRequest newItemRequest, long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID: " + userId + " не найден"));
        if (newItemRequest.getDescription() == null || newItemRequest.getDescription().isEmpty()) {
            throw new ValidationException("Описание должно быть указано");
        }
        ItemRequest itemRequest = ItemRequestMapper.mapToItemRequest(newItemRequest);
        itemRequest.setAuthor(user);
        itemRequest.setCreated(LocalDateTime.now());
        itemRequestRepository.save(itemRequest);
        return ItemRequestMapper.mapToItemRequestDto(itemRequest);
    }

    @Override
    public ItemRequestDTO getItemRequest(long requestId) {
        ItemRequestDTO dto = ItemRequestMapper.mapToItemRequestDto(
                itemRequestRepository.findById(requestId).orElseThrow(() ->
                        new NotFoundException("Запрос с ID: " + requestId + " не найден")));
        dto.setItems(itemsToItemRequest(requestId));
        return dto;
    }

    @Override
    public List<ItemRequestDTO> getItemRequests(long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID: " + userId + " не найден"));
        List<ItemRequestDTO> itemRequests = itemRequestRepository.findByAuthorIdOrderByCreatedDesc(userId)
                .stream()
                .map(ItemRequestMapper::mapToItemRequestDto)
                .toList();
        itemRequests.forEach(itemRequest ->
                itemRequest.setItems(itemsToItemRequest(itemRequest.getId())));
        return itemRequests;
    }

    @Override
    public List<ItemRequestDTO> getAllItemRequests() {
        List<ItemRequestDTO> itemRequests = itemRequestRepository.findAllByOrderByCreatedDesc()
                .stream()
                .map(ItemRequestMapper::mapToItemRequestDto)
                .toList();
        itemRequests.forEach(itemRequest ->
                itemRequest.setItems(itemsToItemRequest(itemRequest.getId())));
        return itemRequests;
    }

    private List<ItemForRequestDTO> itemsToItemRequest(long requestId) {
        return itemRepository.findByRequestId(requestId)
                .stream()
                .map(ItemMapper::mapToItemForRequestDto)
                .toList();
    }
}