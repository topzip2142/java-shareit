package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.request.dto.ItemRequestDTO;
import ru.practicum.shareit.request.dto.NewItemRequest;

import java.util.Collection;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestService itemRequestService;
    private static final String REQUEST_HEADER_USER_ID = "X-Sharer-User-Id";

    @PostMapping
    public ItemRequestDTO createItemRequest(@RequestHeader(REQUEST_HEADER_USER_ID) long userId,
                                            @RequestBody NewItemRequest newItemRequest) {
        return itemRequestService.createItemRequest(newItemRequest, userId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDTO getItemRequest(@PathVariable long requestId) {
        return itemRequestService.getItemRequest(requestId);
    }

    @GetMapping
    public Collection<ItemRequestDTO> getItemRequests(@RequestHeader(REQUEST_HEADER_USER_ID) long userId) {
        return itemRequestService.getItemRequests(userId);
    }

    @GetMapping("/all")
    public Collection<ItemRequestDTO> getAllItemRequests() {
        return itemRequestService.getAllItemRequests();
    }
}