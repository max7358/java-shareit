package ru.practicum.shareit.booking;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingSaveDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;

@Service
public class BookingService {
    BookingRepository repository;
    UserService userService;
    ItemService itemService;

    public BookingService(BookingRepository repository, UserService userService, ItemService itemService) {
        this.repository = repository;
        this.userService = userService;
        this.itemService = itemService;
    }

    public BookingDto createBooking(Long userId, BookingSaveDto bookingSaveDto) {
        UserDto userDto = userService.getUserById(userId);
        ItemDto itemDto = itemService.getItem(bookingSaveDto.getItemId());
        if (bookingSaveDto.getStart().isEqual(bookingSaveDto.getEnd())) {
            throw new BadRequestException("Start and end are equal");
        }
        if (bookingSaveDto.getStart().isAfter(bookingSaveDto.getEnd())) {
            throw new BadRequestException("Start is after end");
        }
        if (Boolean.FALSE.equals(itemDto.getAvailable())) {
            throw new BadRequestException("Item is not available");
        }
        Booking booking = BookingMapper.toBooking(bookingSaveDto);
        booking.setBooker(UserMapper.toUser(userDto));
        booking.setItem(ItemMapper.toItem(itemDto));
        booking.setStatus(BookingStatus.WAITING);
        return BookingMapper.toBookingDto(repository.save(booking));
    }
}
