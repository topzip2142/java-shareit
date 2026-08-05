package ru.practicum.shareit.item.model;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

@Data
@Builder
public class ItemInfo {
    private Item item;
    private Booking lastBooking;
    private Booking nextBooking;
    private List<Comment> comments;
}
