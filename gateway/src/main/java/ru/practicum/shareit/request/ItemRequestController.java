package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/requests")
public class ItemRequestController {

    private final ItemRequestClient requestClient;
    private static final String REQUEST_HEADER_USER_ID = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody ItemRequestDto dto,
                                         @RequestHeader(REQUEST_HEADER_USER_ID) @NotNull(message = "id пользователя должен быть передан") Long userId) {
        log.info("Gateway: Создание запроса вещи пользователем id={}", userId);
        return requestClient.createRequest(userId, dto);
    }

    @GetMapping
    public ResponseEntity<Object> findAllByUserId(@RequestHeader(REQUEST_HEADER_USER_ID) @NotNull(message = "id пользователя должен быть передан") Long userId) {
        log.info("Gateway: Получение своих запросов пользователем id={}", userId);
        return requestClient.getOwnRequests(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> findAllOfOthers(@RequestHeader(REQUEST_HEADER_USER_ID) @NotNull(message = "id пользователя должен быть передан") Long userId) {
        log.info("Gateway: Получение чужих запросов пользователем id={}", userId);
        return requestClient.getAllRequestsOfOthers(userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> findOne(@PathVariable @NotNull(message = "id запроса должен быть передан") Long requestId,
                                          @RequestHeader(REQUEST_HEADER_USER_ID) @NotNull(message = "id пользователя должен быть передан") Long userId) {
        log.info("Gateway: Получение запроса id={} пользователем id={}", requestId, userId);
        return requestClient.getRequestById(requestId, userId);
    }
}
