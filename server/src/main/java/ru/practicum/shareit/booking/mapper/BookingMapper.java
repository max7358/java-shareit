package ru.practicum.shareit.booking.mapper;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingSaveDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.user.mapper.UserMapper;

public class BookingMapper {
    public static BookingDto toBookingDto(Booking booking) {
        return new BookingDto(booking.getId(), booking.getStart(),
                booking.getEnd(), ItemMapper.toItemDto(booking.getItem()), UserMapper.toUserDto(booking.getBooker()), booking.getStatus());
    }

    public static Booking toBooking(BookingSaveDto bookingSaveDto) {
        return Booking.builder().start(bookingSaveDto.getStart()).end(bookingSaveDto.getEnd()).build();
    }
}
