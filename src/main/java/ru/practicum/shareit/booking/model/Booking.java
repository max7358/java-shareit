package ru.practicum.shareit.booking.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.ZonedDateTime;

@Data
@AllArgsConstructor
public class Booking {
    private Long id;
    private ZonedDateTime start;
    private ZonedDateTime end;
    private Item item;
    private User booker;
    private BookingStatus status;
}
