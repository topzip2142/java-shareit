package ru.practicum.shareit.booking;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingRequest;
import ru.practicum.shareit.booking.model.Booking;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BookingMapper {
    public static BookingDto mapToBookingDto(Booking booking) {
        BookingDto dto = new BookingDto();
        dto.setId(booking.getId());
        dto.setStatus(booking.getStatus());
        dto.setBooker(booking.getBooker());
        dto.setItem(booking.getItem());
        dto.setStart(booking.getStartDate());
        dto.setEnd(booking.getEndDate());
        return dto;
    }

    public static Booking mapToBooking(NewBookingRequest request) {
        Booking booking = new Booking();
        booking.setStartDate(request.getStart());
        booking.setEndDate(request.getEnd());
        return booking;
    }
}