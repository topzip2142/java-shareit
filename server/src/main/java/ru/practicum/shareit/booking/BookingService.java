package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingResponseDTO;
import ru.practicum.shareit.booking.dto.BookingRequestDTO;

import java.util.Collection;

public interface BookingService {
    BookingResponseDTO approveBooking(long bookingId, long ownerId, boolean isApprove);

    BookingResponseDTO getBooking(long bookingId, long userId);

    BookingResponseDTO createBooking(BookingRequestDTO booking, long bookerId);

    Collection<BookingResponseDTO> getUserBookings(long bookerId, String state);

    Collection<BookingResponseDTO> getOwnerBookings(long ownerId, String state);

}