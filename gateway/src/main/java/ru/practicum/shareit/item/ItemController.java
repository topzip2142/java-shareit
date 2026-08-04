package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.item.dto.NewItemRequest;
import ru.practicum.shareit.item.dto.UpdateItemRequest;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemClient itemClient;
    private static final String REQUEST_HEADER_USER_ID = "X-Sharer-User-Id";

    @GetMapping
    public ResponseEntity<Object> getItems(@RequestHeader(REQUEST_HEADER_USER_ID) long userId) {
        return itemClient.getItems(userId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItem(@RequestHeader(REQUEST_HEADER_USER_ID) long userId,
                                          @PathVariable long itemId) {
        return itemClient.getItem(itemId, userId);
    }

    @PostMapping
    public ResponseEntity<Object> createItem(@RequestHeader(REQUEST_HEADER_USER_ID) long userId,
                                             @RequestBody NewItemRequest item) {
        return itemClient.createItem(userId, item);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestHeader(REQUEST_HEADER_USER_ID) long userId,
                                             @RequestBody UpdateItemRequest item,
                                             @PathVariable long itemId) {
        return itemClient.updateItem(itemId, userId, item);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItem(@RequestHeader(REQUEST_HEADER_USER_ID) long userId,
                                             @RequestParam String text) {
        return itemClient.searchItems(userId, text);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader(REQUEST_HEADER_USER_ID) long userId,
                                             @PathVariable long itemId,
                                             @RequestBody Object comment) { // На Gateway можно принять как Object
        return itemClient.addComment(userId, itemId, comment);
    }
}