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
import ru.practicum.shareit.booking.enm.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

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


    @BeforeEach
    void setUp() {
        userDto = userService.createUser(new UserDto(null, "mail@du.tu", "name"));
        ItemDto itemDto = itemService.createItem(userDto.getId(), new ItemDto(null, "item", "item descr",
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
    void getBooking() {
        Long id = service.createBooking(userDto.getId(), bookingSaveDto).getId();

        BookingDto bookingDtoS = service.getBooking(userDto.getId(), id);
        assertThat(bookingDtoS.getId(), equalTo(id));
        assertThat(bookingDtoS.getItem().getId(), equalTo(bookingSaveDto.getItemId()));
        assertThat(bookingDtoS.getBooker().getId(), equalTo(userDto.getId()));
        assertThat(bookingDtoS.getStatus(), equalTo(BookingStatus.WAITING));
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
    void getBookingsByOwner() {
        Long id = service.createBooking(userDto.getId(), bookingSaveDto).getId();

        List<BookingDto> bookingDtoS = service.getBookingsByOwner(userDto.getId(), java.util.Optional.empty());
        assertThat(bookingDtoS.size(), equalTo(1));
        assertThat(bookingDtoS.getFirst().getId(), equalTo(id));
        assertThat(bookingDtoS.getFirst().getItem().getId(), equalTo(bookingSaveDto.getItemId()));
        assertThat(bookingDtoS.getFirst().getBooker().getId(), equalTo(userDto.getId()));
        assertThat(bookingDtoS.getFirst().getStatus(), equalTo(BookingStatus.WAITING));
    }
}
