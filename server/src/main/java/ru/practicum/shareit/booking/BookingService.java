package ru.practicum.shareit.booking;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingSaveDto;
import ru.practicum.shareit.booking.enm.BookingState;
import ru.practicum.shareit.booking.enm.BookingStatus;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class BookingService {
    BookingRepository repository;
    UserService userService;
    ItemService itemService;

    public BookingService(BookingRepository repository, UserService userService, @Lazy ItemService itemService) {
        this.repository = repository;
        this.userService = userService;
        this.itemService = itemService;
    }

    @Transactional
    public BookingDto createBooking(Long userId, BookingSaveDto bookingSaveDto) {
        UserDto userDto = userService.getUserById(userId);
        ItemDto itemDto = itemService.getItem(bookingSaveDto.getItemId(), userId);
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
        booking.setItem(ItemMapper.toItem(itemDto, userDto));
        booking.setStatus(BookingStatus.WAITING);
        return BookingMapper.toBookingDto(repository.save(booking));
    }

    public BookingDto getBooking(Long userId, Long bookingId) {
        Booking booking = getBooking(bookingId);
        if (userId.equals(booking.getBooker().getId()) || userId.equals(booking.getItem().getOwner().getId())) {
            return BookingMapper.toBookingDto(booking);
        } else {
            throw new ForbiddenException("User is not authorized");
        }
    }

    private Booking getBooking(Long bookingId) {
        return repository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking with id:" + bookingId + " not found"));
    }

    @Transactional
    public BookingDto approveBooking(Long userId, Long bookingId, Boolean approved) {
        Booking booking = getBooking(bookingId);
        if (!userId.equals(booking.getItem().getOwner().getId())) {
            throw new ForbiddenException("User is not owner of item");
        }
        if (Boolean.TRUE.equals(approved)) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }
        return BookingMapper.toBookingDto(repository.save(booking));
    }

    public List<BookingDto> getBookings(Long userId, Optional<BookingState> bState) {
        BookingState state = bState.orElse(BookingState.ALL);
        return switch (state) {
            case ALL ->
                    repository.findByBooker_IdOrderByStartDesc(userId).stream().map(BookingMapper::toBookingDto).toList();
            case CURRENT -> repository.findByBooker_IdAndStatusOrderByStartDesc(userId, BookingStatus.APPROVED)
                    .stream().map(BookingMapper::toBookingDto).toList();
            case PAST -> repository.findByBooker_IdAndEndIsBeforeOrderByStartDesc(userId, LocalDateTime.now())
                    .stream().map(BookingMapper::toBookingDto).toList();
            case FUTURE -> repository.findByBooker_IdAndStartIsAfterOrderByStartDesc(userId, LocalDateTime.now())
                    .stream().map(BookingMapper::toBookingDto).toList();
            case WAITING -> repository.findByBooker_IdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING)
                    .stream().map(BookingMapper::toBookingDto).toList();
            case REJECTED -> repository.findByBooker_IdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED)
                    .stream().map(BookingMapper::toBookingDto).toList();
        };
    }

    public List<BookingDto> getBookingsByOwner(Long userId, Optional<BookingState> bState) {
        BookingState state = bState.orElse(BookingState.ALL);
        User user = UserMapper.toUser(userService.getUserById(userId));
        return switch (state) {
            case ALL ->
                    repository.findBookingsByItemOwnerOrderByStartDesc(user).stream().map(BookingMapper::toBookingDto).toList();
            case CURRENT -> repository.findBookingsByItemOwnerAndStatusOrderByStartDesc(user, BookingStatus.APPROVED)
                    .stream().map(BookingMapper::toBookingDto).toList();
            case PAST -> repository.findBookingsByItemOwnerAndEndIsBeforeOrderByStartDesc(user, LocalDateTime.now())
                    .stream().map(BookingMapper::toBookingDto).toList();
            case FUTURE -> repository.findBookingsByItemOwnerAndStartIsAfterOrderByStartDesc(user, LocalDateTime.now())
                    .stream().map(BookingMapper::toBookingDto).toList();
            case WAITING -> repository.findBookingsByItemOwnerAndStatusOrderByStartDesc(user, BookingStatus.WAITING)
                    .stream().map(BookingMapper::toBookingDto).toList();
            case REJECTED -> repository.findBookingsByItemOwnerAndStatusOrderByStartDesc(user, BookingStatus.REJECTED)
                    .stream().map(BookingMapper::toBookingDto).toList();
        };
    }

    public List<BookingDto> getBookingsByUserAndItem(Long userId, Long itemId) {
        return repository.findByBooker_IdAndItem_IdAndStatusAndEndIsBefore(userId, itemId, BookingStatus.APPROVED, LocalDateTime.now())
                .stream().map(BookingMapper::toBookingDto).toList();
    }

    public List<BookingDto> getBookingsByItem(Long itemId) {
        return repository.findByItem_Id(itemId).stream().map(BookingMapper::toBookingDto).toList();
    }
}
