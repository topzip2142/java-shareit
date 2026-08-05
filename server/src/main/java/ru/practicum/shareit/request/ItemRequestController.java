package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/requests")
public class ItemRequestController {

    private final ItemRequestService requestService;
    private static final String REQUEST_HEADER_USER_ID = "X-Sharer-User-Id";

    @PostMapping
    public ItemRequestDto create(@RequestBody ItemRequestDto dto,
                                 @RequestHeader(REQUEST_HEADER_USER_ID) Long userId) {
        ItemRequest request = ItemRequestMapper.toItemRequest(dto);
        ItemRequest savedRequest = requestService.create(request, userId);
        //При создании запроса вещей в запросе пока нет, потому пуляем пустой Лист
        return ItemRequestMapper.toItemRequestDto(savedRequest, List.of());
    }

    @GetMapping
    public List<ItemRequestDto> findAllByUserId(@RequestHeader(REQUEST_HEADER_USER_ID) Long userId) {
        List<ItemRequest> requests = requestService.findAllByUserId(userId);
        List<Item> items = requestService.findItemsForRequests(requests);

        return requests.stream()
                .map(request -> ItemRequestMapper.toItemRequestDto(request, items))
                .toList();
    }

    @GetMapping("/all")
    public List<ItemRequestDto> findAllOfOthers(@RequestHeader(REQUEST_HEADER_USER_ID) Long userId) {
        List<ItemRequest> requests = requestService.findAllOfOthers(userId);
        List<Item> items = requestService.findItemsForRequests(requests);

        return requests.stream()
                .map(request -> ItemRequestMapper.toItemRequestDto(request, items))
                .toList();
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto findOne(@PathVariable Long requestId,
                                  @RequestHeader(REQUEST_HEADER_USER_ID) Long userId) {
        ItemRequest request = requestService.findOne(requestId, userId);
        List<Item> items = requestService.findItemsForRequests(List.of(request));
        return ItemRequestMapper.toItemRequestDto(request, items);
    }
}
