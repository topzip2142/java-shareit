package ru.practicum.shareit.booking;

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
import ru.practicum.shareit.booking.dto.NewBookingRequest;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingClient bookingClient;

    private static final String REQUEST_HEADER_USER_ID = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<Object> createBooking(@RequestHeader(REQUEST_HEADER_USER_ID) long userId,
                                                @RequestBody NewBookingRequest booking) {
        return bookingClient.createBooking(booking, userId);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approveBooking(@RequestHeader(REQUEST_HEADER_USER_ID) long userId,
                                                 @PathVariable long bookingId,
                                                 @RequestParam boolean approved) {
        return bookingClient.approveBooking(userId, bookingId, approved);
    }


    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(@RequestHeader(REQUEST_HEADER_USER_ID) long userId,
                                             @PathVariable long bookingId) {
        return bookingClient.getBooking(userId, bookingId);
    }

    @GetMapping()
    public ResponseEntity<Object> getAllBookings(@RequestHeader(REQUEST_HEADER_USER_ID) long userId,
                                                 @RequestParam(defaultValue = "ALL") String state) {
        return bookingClient.getAllBookings(userId, state);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getAllBookingsByOwner(@RequestHeader(REQUEST_HEADER_USER_ID) long userId,
                                                        @RequestParam(defaultValue = "ALL") String state) {
        return bookingClient.getOwnerBookings(userId, state);
    }
}