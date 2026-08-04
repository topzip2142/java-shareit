package ru.practicum.shareit;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingServiceImp;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dto.BookingResponseDTO;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;


@ActiveProfiles("test")
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@RequiredArgsConstructor(onConstructor_ = @Autowired)

public class BookingServiceTest {
    private final BookingServiceImp bookingServiceImp;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;

    @Test
    void getUserBookings() {
        Booking booking1 = new Booking();
        Booking booking2 = new Booking();
        Booking booking3 = new Booking();
        Item item = new Item();
        User booker = new User();
        User owner = new User();

        owner.setId(1L);
        owner.setName("Хозяин");
        owner.setEmail("owner@mail.com");
        userRepository.save(owner);

        booker.setId(2L);
        booker.setName("Букер");
        booker.setEmail("booker@mail.com");
        userRepository.save(booker);

        item.setId(1L);
        item.setOwner(owner);
        item.setDescription("описание вещи");
        item.setName("Вещь");
        item.setAvailable(true);
        itemRepository.save(item);

        booking1.setId(1L);
        booking1.setBooker(booker);
        booking1.setItem(item);
        booking1.setStatus(Status.WAITING);
        booking1.setStartDate(LocalDateTime.now().plusDays(1).withNano(0));
        booking1.setEndDate(LocalDateTime.now().plusDays(2).withNano(0));
        bookingRepository.save(booking1);

        booking2.setId(2L);
        booking2.setBooker(booker);
        booking2.setItem(item);
        booking2.setStatus(Status.APPROVED);
        booking2.setStartDate(LocalDateTime.now().plusDays(3).withNano(0));
        booking2.setEndDate(LocalDateTime.now().plusDays(4).withNano(0));
        bookingRepository.save(booking2);

        booking3.setId(3L);
        booking3.setBooker(booker);
        booking3.setItem(item);
        booking3.setStatus(Status.WAITING);
        booking3.setStartDate(LocalDateTime.now().plusDays(5).withNano(0));
        booking3.setEndDate(LocalDateTime.now().plusDays(5).withNano(0));
        bookingRepository.save(booking3);

        List<BookingResponseDTO> bookings = bookingServiceImp.getUserBookings(booker.getId(), "ALL").stream().toList();

        assertThat(bookings.size(), equalTo(3));
        assertThat(bookings.get(0).getId(), equalTo(booking3.getId()));
        assertThat(bookings.get(0).getItem(), equalTo(booking3.getItem()));
        assertThat(bookings.get(0).getStatus(), equalTo(booking3.getStatus()));
        assertThat(bookings.get(0).getBooker(), equalTo(booking3.getBooker()));
        assertThat(bookings.get(0).getStart(), equalTo(booking3.getStartDate()));
        assertThat(bookings.get(0).getEnd(), equalTo(booking3.getEndDate()));

        assertThat(bookings.get(1).getId(), equalTo(booking2.getId()));
        assertThat(bookings.get(1).getItem(), equalTo(booking2.getItem()));
        assertThat(bookings.get(1).getStatus(), equalTo(booking2.getStatus()));
        assertThat(bookings.get(1).getBooker(), equalTo(booking2.getBooker()));
        assertThat(bookings.get(1).getStart(), equalTo(booking2.getStartDate()));
        assertThat(bookings.get(1).getEnd(), equalTo(booking2.getEndDate()));

        assertThat(bookings.get(2).getId(), equalTo(booking1.getId()));
        assertThat(bookings.get(2).getItem(), equalTo(booking1.getItem()));
        assertThat(bookings.get(2).getStatus(), equalTo(booking1.getStatus()));
        assertThat(bookings.get(2).getBooker(), equalTo(booking1.getBooker()));
        assertThat(bookings.get(2).getStart(), equalTo(booking1.getStartDate()));
        assertThat(bookings.get(2).getEnd(), equalTo(booking1.getEndDate()));
    }
}