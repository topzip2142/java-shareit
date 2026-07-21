package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingRequest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.NoAccessException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collection;

@Service
@RequiredArgsConstructor
public class BookingServiceImp implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public BookingDto createBooking(NewBookingRequest request, long bookerId) {
        User booker = userRepository.findById(bookerId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID: " + bookerId + " не найден"));
        long itemId = request.getItemId();
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с ID: " + itemId + " не найдена"));
        if (!item.isAvailable()) {
            throw new ValidationException("Вещь с ID: " + itemId + " недоступна для брони");
        }
        checkBooking(request);
        Booking booking = BookingMapper.mapToBooking(request);
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(Status.WAITING);
        bookingRepository.save(booking);
        return BookingMapper.mapToBookingDto(booking);
    }

    @Override
    public BookingDto approveBooking(long bookingId, long ownerId, boolean isApprove) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Брони с ID: " + bookingId + " не найдена"));
        Item item = booking.getItem();
        if (ownerId != item.getOwner().getId()) {
            throw new NoAccessException("Пользователь не является хозяином вещи");
        }
        if (isApprove) {
            booking.setStatus(Status.APPROVED);
        } else {
            booking.setStatus(Status.REJECTED);
        }
        return BookingMapper.mapToBookingDto(booking);
    }

    @Override
    public BookingDto getBooking(long bookingId, long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Брони с ID: " + bookingId + " не найдена"));
        Item item = booking.getItem();
        if (userId == item.getOwner().getId() || userId == booking.getBooker().getId()) {
            return BookingMapper.mapToBookingDto(booking);
        } else {
            throw new NoAccessException("Нет прав на просмотр брони");
        }
    }

    @Override
    public Collection<BookingDto> getUserBookings(long userId, String state) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID: " + userId + " не найден"));
        Collection<Booking> bookings;
        String upperState = state.toUpperCase().trim();
        LocalDateTime now = LocalDateTime.now();
        bookings = switch (upperState) {
            case "ALL" -> bookingRepository.findByBookerIdOrderByStartDateDesc(userId);
            case "CURRENT" -> bookingRepository.findByBookerIdAndStartDateIsBeforeAndEndDateIsAfterOrderByStartDateDesc(
                    userId, now, now);
            case "PAST" -> bookingRepository.findByBookerIdAndEndDateIsBeforeOrderByStartDateDesc(userId, now);
            case "FUTURE" -> bookingRepository.findByBookerIdAndStartDateIsAfterOrderByStartDateDesc(userId, now);
            case "WAITING" -> bookingRepository.findByBookerIdAndStatusOrderByStartDateDesc(userId, Status.WAITING);
            case "REJECTED" -> bookingRepository.findByBookerIdAndStatusOrderByStartDateDesc(userId, Status.REJECTED);
            default -> throw new ValidationException("Недопустимое значение state");
        };
        return bookings.stream()
                .map(BookingMapper::mapToBookingDto)
                .toList();
    }

    @Override
    public Collection<BookingDto> getOwnerBookings(long ownerId, String state) {
        userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID: " + ownerId + " не найден"));
        Collection<Booking> bookings;
        String upperState = state.toUpperCase().trim();
        LocalDateTime now = LocalDateTime.now();
        bookings = switch (upperState) {
            case "ALL" -> bookingRepository.findAllOwnerBookings(ownerId);
            case "CURRENT" -> bookingRepository.findCurrentOwnerBookings(ownerId, now);
            case "PAST" -> bookingRepository.findPastOwnerBookings(ownerId, now);
            case "FUTURE" -> bookingRepository.findFutureOwnerBookings(ownerId, now);
            case "WAITING" -> bookingRepository.findByStatusOwnerBookings(ownerId, Status.WAITING);
            case "REJECTED" -> bookingRepository.findByStatusOwnerBookings(ownerId, Status.REJECTED);
            default -> throw new ValidationException("Недопустимое значение state");
        };
        return bookings.stream()
                .map(BookingMapper::mapToBookingDto)
                .toList();
    }

    private void checkBooking(NewBookingRequest request) {
        if (request.getStart() == null || request.getStart().toString().isEmpty()) {
            throw new ValidationException("Должны быть указаны ДатаВремя начала брони");
        }
        if (request.getStart().isBefore(LocalDateTime.now())) {
            throw new ValidationException("ДатаВремя начала брони не должны быть в прошлом");
        }
        if (request.getEnd() == null || request.getEnd().toString().isEmpty()) {
            throw new ValidationException("Должны быть указаны ДатаВремя окончания брони");
        }
        if (request.getEnd().isBefore(LocalDateTime.now())) {
            throw new ValidationException("ДатаВремя окончания брони не должны быть в прошлом");
        }
        if (request.getStart().equals(request.getEnd())) {
            throw new ValidationException("ДатаВремя начала и окончания брони не должны быть равными");
        }
    }
}