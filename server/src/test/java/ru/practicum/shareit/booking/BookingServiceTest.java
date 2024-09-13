package ru.practicum.shareit.booking;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingSaveDto;
import ru.practicum.shareit.booking.enm.BookingState;
import ru.practicum.shareit.booking.enm.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest
class BookingServiceTest {
    @Autowired
    BookingService service;

    @Autowired
    private EntityManager em;

    @Autowired
    private UserService userService;

    @Autowired
    private ItemService itemService;

    private BookingSaveDto bookingSaveDto;
    private UserDto userDto;
    private ItemDto itemDto;


    @BeforeEach
    void setUp() {
        userDto = userService.createUser(new UserDto(null, "mail@du.tu", "name"));
        itemDto = itemService.createItem(userDto.getId(), new ItemDto(null, "item", "item descr",
                true, null, userDto.getId()));
        bookingSaveDto = new BookingSaveDto(null, LocalDateTime.now().plusMinutes(1), LocalDateTime.now().plusMinutes(10),
                itemDto.getId());
    }

    @Test
    void createBooking() {
        Long id = service.createBooking(userDto.getId(), bookingSaveDto).getId();

        TypedQuery<Booking> query = em.createQuery("select booking from Booking booking where booking.id = :id", Booking.class);
        Booking booking = query.setParameter("id", id).getSingleResult();
        assertThat(booking.getId(), equalTo(id));
        assertThat(booking.getItem().getId(), equalTo(bookingSaveDto.getItemId()));
        assertThat(booking.getBooker().getId(), equalTo(userDto.getId()));
        assertThat(booking.getStatus(), equalTo(BookingStatus.WAITING));
    }

    @Test
    void createBookingExceptionStart() {
        bookingSaveDto.setEnd(LocalDateTime.now().minusMinutes(10));
        assertThrows(BadRequestException.class, () -> service.createBooking(userDto.getId(), bookingSaveDto));
    }

    @Test
    void createBookingExceptionEqual() {
        bookingSaveDto.setStart(LocalDateTime.now());
        bookingSaveDto.setEnd(bookingSaveDto.getStart());
        assertThrows(BadRequestException.class, () -> service.createBooking(userDto.getId(), bookingSaveDto));
    }

    @Test
    void createBookingExceptionItem() {
        itemService.updateItem(userDto.getId(), itemDto.getId(), ItemDto.builder().available(false).build());
        assertThrows(BadRequestException.class, () -> service.createBooking(userDto.getId(), bookingSaveDto));
    }

    @Test
    void getBooking() {
        Long id = service.createBooking(userDto.getId(), bookingSaveDto).getId();

        BookingDto bookingDtoS = service.getBooking(userDto.getId(), id);
        assertThat(bookingDtoS.getId(), equalTo(id));
        assertThat(bookingDtoS.getItem().getId(), equalTo(bookingSaveDto.getItemId()));
        assertThat(bookingDtoS.getBooker().getId(), equalTo(userDto.getId()));
        assertThat(bookingDtoS.getStatus(), equalTo(BookingStatus.WAITING));
    }

    @Test
    void getBookingAuthException() {
        Long id = service.createBooking(userDto.getId(), bookingSaveDto).getId();

        assertThrows(ForbiddenException.class, () -> service.getBooking(99L, id));
    }

    @Test
    void getBookingNotFoundException() {
        assertThrows(NotFoundException.class, () -> service.getBooking(99L, 99L));
    }

    @Test
    void approveBooking() {
        Long id = service.createBooking(userDto.getId(), bookingSaveDto).getId();
        service.approveBooking(userDto.getId(), id, true);

        BookingDto bookingDtoS = service.getBooking(userDto.getId(), id);
        assertThat(bookingDtoS.getId(), equalTo(id));
        assertThat(bookingDtoS.getItem().getId(), equalTo(bookingSaveDto.getItemId()));
        assertThat(bookingDtoS.getBooker().getId(), equalTo(userDto.getId()));
        assertThat(bookingDtoS.getStatus(), equalTo(BookingStatus.APPROVED));
    }

    @Test
    void approveBookingException() {
        Long id = service.createBooking(userDto.getId(), bookingSaveDto).getId();
        assertThrows(ForbiddenException.class, () -> service.approveBooking(99L, id, true));
    }

    @Test
    void getBookings() {
        Long id = service.createBooking(userDto.getId(), bookingSaveDto).getId();

        List<BookingDto> bookingDtoS = service.getBookings(userDto.getId(), java.util.Optional.empty());
        assertThat(bookingDtoS.size(), equalTo(1));
        assertThat(bookingDtoS.getFirst().getId(), equalTo(id));
        assertThat(bookingDtoS.getFirst().getItem().getId(), equalTo(bookingSaveDto.getItemId()));
        assertThat(bookingDtoS.getFirst().getBooker().getId(), equalTo(userDto.getId()));
        assertThat(bookingDtoS.getFirst().getStatus(), equalTo(BookingStatus.WAITING));
    }

    @Test
    void getBookingsByStateWaiting() {
        Long id = service.createBooking(userDto.getId(), bookingSaveDto).getId();

        List<BookingDto> bookingDtoS = service.getBookings(userDto.getId(), Optional.of(BookingState.WAITING));
        assertThat(bookingDtoS.size(), equalTo(1));
        assertThat(bookingDtoS.getFirst().getId(), equalTo(id));
        assertThat(bookingDtoS.getFirst().getItem().getId(), equalTo(bookingSaveDto.getItemId()));
        assertThat(bookingDtoS.getFirst().getBooker().getId(), equalTo(userDto.getId()));
        assertThat(bookingDtoS.getFirst().getStatus(), equalTo(BookingStatus.WAITING));
    }

    @Test
    void getBookingsByStateRejected() {
        Long id = service.createBooking(userDto.getId(), bookingSaveDto).getId();
        service.approveBooking(userDto.getId(), id, false);

        List<BookingDto> bookingDtoS = service.getBookings(userDto.getId(), Optional.of(BookingState.REJECTED));
        assertThat(bookingDtoS.size(), equalTo(1));
        assertThat(bookingDtoS.getFirst().getId(), equalTo(id));
        assertThat(bookingDtoS.getFirst().getItem().getId(), equalTo(bookingSaveDto.getItemId()));
        assertThat(bookingDtoS.getFirst().getBooker().getId(), equalTo(userDto.getId()));
        assertThat(bookingDtoS.getFirst().getStatus(), equalTo(BookingStatus.REJECTED));
    }

    @Test
    void getBookingsByStateFuture() {
        bookingSaveDto.setStart(LocalDateTime.now().plusMinutes(1));
        Long id = service.createBooking(userDto.getId(), bookingSaveDto).getId();

        List<BookingDto> bookingDtoS = service.getBookings(userDto.getId(), Optional.of(BookingState.FUTURE));
        assertThat(bookingDtoS.size(), equalTo(1));
        assertThat(bookingDtoS.getFirst().getId(), equalTo(id));
        assertThat(bookingDtoS.getFirst().getItem().getId(), equalTo(bookingSaveDto.getItemId()));
        assertThat(bookingDtoS.getFirst().getBooker().getId(), equalTo(userDto.getId()));
        assertThat(bookingDtoS.getFirst().getStatus(), equalTo(BookingStatus.WAITING));
    }

    @Test
    void getBookingsByStatePast() {
        bookingSaveDto.setStart(LocalDateTime.now().minusMinutes(10));
        bookingSaveDto.setEnd(LocalDateTime.now().minusMinutes(5));
        Long id = service.createBooking(userDto.getId(), bookingSaveDto).getId();

        List<BookingDto> bookingDtoS = service.getBookings(userDto.getId(), Optional.of(BookingState.PAST));
        assertThat(bookingDtoS.size(), equalTo(1));
        assertThat(bookingDtoS.getFirst().getId(), equalTo(id));
        assertThat(bookingDtoS.getFirst().getItem().getId(), equalTo(bookingSaveDto.getItemId()));
        assertThat(bookingDtoS.getFirst().getBooker().getId(), equalTo(userDto.getId()));
        assertThat(bookingDtoS.getFirst().getStatus(), equalTo(BookingStatus.WAITING));
    }

    @Test
    void getBookingsByOwner() {
        Long id = service.createBooking(userDto.getId(), bookingSaveDto).getId();

        List<BookingDto> bookingDtoS = service.getBookingsByOwner(userDto.getId(), java.util.Optional.empty());
        assertThat(bookingDtoS.size(), equalTo(1));
        assertThat(bookingDtoS.getFirst().getId(), equalTo(id));
        assertThat(bookingDtoS.getFirst().getItem().getId(), equalTo(bookingSaveDto.getItemId()));
        assertThat(bookingDtoS.getFirst().getBooker().getId(), equalTo(userDto.getId()));
        assertThat(bookingDtoS.getFirst().getStatus(), equalTo(BookingStatus.WAITING));
    }

    @Test
    void getBookingsByOwnerCurrent() {
        Long id = service.createBooking(userDto.getId(), bookingSaveDto).getId();
        service.approveBooking(userDto.getId(), id, true);

        List<BookingDto> bookingDtoS = service.getBookingsByOwner(userDto.getId(), Optional.of(BookingState.CURRENT));
        assertThat(bookingDtoS.size(), equalTo(1));
        assertThat(bookingDtoS.getFirst().getId(), equalTo(id));
        assertThat(bookingDtoS.getFirst().getItem().getId(), equalTo(bookingSaveDto.getItemId()));
        assertThat(bookingDtoS.getFirst().getBooker().getId(), equalTo(userDto.getId()));
        assertThat(bookingDtoS.getFirst().getStatus(), equalTo(BookingStatus.APPROVED));
    }

    @Test
    void getBookingsByOwnerRejected() {
        Long id = service.createBooking(userDto.getId(), bookingSaveDto).getId();
        service.approveBooking(userDto.getId(), id, false);

        List<BookingDto> bookingDtoS = service.getBookingsByOwner(userDto.getId(), Optional.of(BookingState.REJECTED));
        assertThat(bookingDtoS.size(), equalTo(1));
        assertThat(bookingDtoS.getFirst().getId(), equalTo(id));
        assertThat(bookingDtoS.getFirst().getItem().getId(), equalTo(bookingSaveDto.getItemId()));
        assertThat(bookingDtoS.getFirst().getBooker().getId(), equalTo(userDto.getId()));
        assertThat(bookingDtoS.getFirst().getStatus(), equalTo(BookingStatus.REJECTED));
    }

    @Test
    void getBookingsByOwnerFuture() {
        bookingSaveDto.setStart(LocalDateTime.now().plusMinutes(1));
        Long id = service.createBooking(userDto.getId(), bookingSaveDto).getId();

        List<BookingDto> bookingDtoS = service.getBookingsByOwner(userDto.getId(), Optional.of(BookingState.FUTURE));
        assertThat(bookingDtoS.size(), equalTo(1));
        assertThat(bookingDtoS.getFirst().getId(), equalTo(id));
        assertThat(bookingDtoS.getFirst().getItem().getId(), equalTo(bookingSaveDto.getItemId()));
        assertThat(bookingDtoS.getFirst().getBooker().getId(), equalTo(userDto.getId()));
        assertThat(bookingDtoS.getFirst().getStatus(), equalTo(BookingStatus.WAITING));
    }

    @Test
    void getBookingsByOwnerPast() {
        bookingSaveDto.setStart(LocalDateTime.now().minusMinutes(10));
        bookingSaveDto.setEnd(LocalDateTime.now().minusMinutes(5));
        Long id = service.createBooking(userDto.getId(), bookingSaveDto).getId();

        List<BookingDto> bookingDtoS = service.getBookingsByOwner(userDto.getId(), Optional.of(BookingState.PAST));
        assertThat(bookingDtoS.size(), equalTo(1));
        assertThat(bookingDtoS.getFirst().getId(), equalTo(id));
        assertThat(bookingDtoS.getFirst().getItem().getId(), equalTo(bookingSaveDto.getItemId()));
        assertThat(bookingDtoS.getFirst().getBooker().getId(), equalTo(userDto.getId()));
        assertThat(bookingDtoS.getFirst().getStatus(), equalTo(BookingStatus.WAITING));
    }
}
