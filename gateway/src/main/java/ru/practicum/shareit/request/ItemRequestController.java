package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.request.dto.NewItemRequestRequest;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {

    private final ItemRequestClient itemRequestClient;
    private static final String REQUEST_HEADER_USER_ID = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<Object> createItemRequest(@RequestHeader(REQUEST_HEADER_USER_ID) long userId,
                                                    @RequestBody NewItemRequestRequest newItemRequest) {
        return itemRequestClient.createItemRequest(userId, newItemRequest);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getItemRequest(@RequestHeader(REQUEST_HEADER_USER_ID) long userId,
                                                 @PathVariable long requestId) {
        return itemRequestClient.getItemRequest(userId, requestId);
    }

    @GetMapping
    public ResponseEntity<Object> getItemRequests(@RequestHeader(REQUEST_HEADER_USER_ID) long userId) {
        return itemRequestClient.getItemRequests(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllItemRequests(@RequestHeader(REQUEST_HEADER_USER_ID) long userId) {
        return itemRequestClient.getAllItemRequests(userId);
    }
}