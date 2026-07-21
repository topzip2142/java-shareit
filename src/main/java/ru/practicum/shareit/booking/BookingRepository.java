package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByBookerIdOrderByStartDateDesc(Long bookerId);

    List<Booking> findByBookerIdAndStartDateIsBeforeAndEndDateIsAfterOrderByStartDateDesc(long bookerId, LocalDateTime start,
                                                                                          LocalDateTime end);

    List<Booking> findByBookerIdAndEndDateIsBeforeOrderByStartDateDesc(long bookerId, LocalDateTime end);

    List<Booking> findByBookerIdAndStartDateIsAfterOrderByStartDateDesc(long bookerId, LocalDateTime start);

    List<Booking> findByBookerIdAndStatusOrderByStartDateDesc(long bookerId, Status status);

    @Query("select b from Booking b "
            + "where b.item.owner.id = ?1 "
            + "order by b.startDate desc")
    List<Booking> findAllOwnerBookings(long ownerId);

    @Query("select b from Booking b "
            + "where b.item.owner.id = ?1 "
            + "and b.startDate <= ?2 "
            + "and b.endDate >= ?2 "
            + "order by b.startDate desc")
    List<Booking> findCurrentOwnerBookings(long ownerId, LocalDateTime now);

    @Query("select b from Booking b "
            + "where b.item.owner.id = ?1 "
            + "and b.endDate < ?2 "
            + "order by b.startDate desc")
    List<Booking> findPastOwnerBookings(long ownerId, LocalDateTime end);

    @Query("select b from Booking b "
            + "where b.item.owner.id = ?1 "
            + "and b.startDate > ?2 "
            + "order by b.startDate desc")
    List<Booking> findFutureOwnerBookings(long ownerId, LocalDateTime end);

    @Query("select b from Booking b "
            + "where b.item.owner.id = ?1 "
            + "and b.status like ?2 "
            + "order by b.startDate desc")
    List<Booking> findByStatusOwnerBookings(long ownerId, Status status);

    List<Booking> findByItemIdOrderByStartDateDesc(long itemId);

    @Query("select b from Booking b "
            + "where b.item.id = ?1 "
            + "and b.status = ?2 "
            + "and b.endDate < ?3 "
            + "order by b.endDate desc")
    List<Booking> findLastBookingOfItem(long itemId, Status approvedStatus, LocalDateTime now);

    @Query("select b from Booking b "
            + "where b.item.id = ?1 "
            + "and b.status != ?2 "
            + "and b.startDate >= ?3 "
            + "order by b.startDate asc")
    List<Booking> findNextBookingOfItem(long itemId, Status rejectedStatus, LocalDateTime now);
}