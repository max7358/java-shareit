package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.ZonedDateTime;

@Data
@AllArgsConstructor
public class BookingDto {
    private Long id;
    private ZonedDateTime start;
    private ZonedDateTime end;
    private ItemDto itemDto;
    private UserDto bookerDto;
    private BookingStatus status;
}
