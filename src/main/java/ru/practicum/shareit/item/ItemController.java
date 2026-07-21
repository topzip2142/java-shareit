package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;

import java.util.Collection;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    private static final String REQUEST_HEADER_USER_ID = "X-Sharer-User-Id";

    @GetMapping
    public Collection<ItemBookingDto> getItems(@RequestHeader(REQUEST_HEADER_USER_ID) long userId) {
        return itemService.getItems(userId);
    }

    @GetMapping("/{itemId}")
    public ItemBookingDto getItem(@PathVariable long itemId) {
        return itemService.getItem(itemId);
    }

    @PostMapping
    public ItemDto createItem(@RequestHeader(REQUEST_HEADER_USER_ID) long userId,
                              @RequestBody NewItemRequest item) {
        return itemService.createItem(item, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader(REQUEST_HEADER_USER_ID) long userId,
                              @RequestBody UpdateItemRequest item,
                              @PathVariable long itemId) {
        return itemService.updateItem(itemId, item, userId);
    }

    @GetMapping("/search")
    public Collection<ItemDto> searchItem(@RequestHeader(REQUEST_HEADER_USER_ID) long userId,
                                          @RequestParam String text) {
        return itemService.search(userId, text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader(REQUEST_HEADER_USER_ID) long userId,
                                 @PathVariable long itemId,
                                 @RequestBody Comment text) {
        return itemService.addComment(userId,itemId,text);
    }
}