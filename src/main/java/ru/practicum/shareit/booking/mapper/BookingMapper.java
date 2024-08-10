package ru.practicum.shareit.booking.mapper;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.user.mapper.UserMapper;

public class BookingMapper {
    public static BookingDto toBookingDto(Booking booking) {
        return new BookingDto(booking.getId(), booking.getStart(),
                booking.getEnd(), ItemMapper.toItemDto(booking.getItem()), UserMapper.toUserDto(booking.getBooker()), booking.getStatus());
    }

    public static Booking toBooking(BookingDto bookingDto) {
        return new Booking(bookingDto.getId(), bookingDto.getStart(),
                bookingDto.getEnd(), ItemMapper.toItem(bookingDto.getItemDto()), UserMapper.toUser(bookingDto.getBookerDto()), bookingDto.getStatus());
    }
}
