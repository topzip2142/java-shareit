package ru.practicum.shareit.booking;

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
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingRequest;

import java.util.Collection;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;
    private static final String REQUEST_HEADER_USER_ID = "X-Sharer-User-Id";

    @PostMapping
    public BookingDto createItem(@RequestHeader(REQUEST_HEADER_USER_ID) long userId,
                                 @RequestBody NewBookingRequest booking) {
        return bookingService.createBooking(booking, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approveBooking(@RequestHeader(REQUEST_HEADER_USER_ID) long userId,
                                     @PathVariable long bookingId,
                                     @RequestParam boolean approved) {
        return bookingService.approveBooking(bookingId, userId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBooking(@RequestHeader(REQUEST_HEADER_USER_ID) long userId,
                                 @PathVariable long bookingId) {
        return bookingService.getBooking(bookingId, userId);
    }

    @GetMapping()
    public Collection<BookingDto> getAllBookings(@RequestHeader(REQUEST_HEADER_USER_ID) long userId,
                                                 @RequestParam(defaultValue = "ALL") String state) {
        return bookingService.getUserBookings(userId, state);
    }

    @GetMapping("/owner")
    public Collection<BookingDto> getAllBookingsByOwner(@RequestHeader(REQUEST_HEADER_USER_ID) long userId,
                                                        @RequestParam(defaultValue = "ALL") String state) {
        return bookingService.getOwnerBookings(userId, state);
    }
}