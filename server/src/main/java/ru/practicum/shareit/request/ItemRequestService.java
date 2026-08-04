package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestDTO;
import ru.practicum.shareit.request.dto.NewItemRequest;

import java.util.Collection;

public interface ItemRequestService {
    ItemRequestDTO getItemRequest(long id);

    Collection<ItemRequestDTO> getItemRequests(long userId);

    Collection<ItemRequestDTO> getAllItemRequests();

    ItemRequestDTO createItemRequest(NewItemRequest itemRequest, long userId);

}