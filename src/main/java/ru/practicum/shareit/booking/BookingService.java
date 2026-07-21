package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingRequest;

import java.util.Collection;

public interface BookingService {
    BookingDto approveBooking(long bookingId, long ownerId, boolean isApprove);

    BookingDto getBooking(long bookingId, long userId);

    BookingDto createBooking(NewBookingRequest booking, long bookerId);

    Collection<BookingDto> getUserBookings(long bookerId, String state);

    Collection<BookingDto> getOwnerBookings(long ownerId, String state);

}