package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.enm.BookingStatus;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
class BookingControllerTest {
    @MockBean
    BookingService bookingService;

    @Autowired
    ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    private BookingDto bookingDto;
    private final String header = "X-Sharer-User-Id";

    @BeforeEach
    void setUp() {
        UserDto userDto = new UserDto(
                1L,
                "john.doe@mail.com",
                "John");
        ItemDto itemDto = new ItemDto(1L, "name", "description",
                true, null, 1L);
        bookingDto = new BookingDto(
                1L,
                LocalDateTime.now(), LocalDateTime.now().plusHours(1),
                itemDto, userDto, BookingStatus.WAITING);
    }

    @Test
    void createBooking() throws Exception {
        when(bookingService.createBooking(anyLong(), any())).thenReturn(bookingDto);
        mvc.perform(post("/bookings").content(mapper.writeValueAsString(bookingDto)).header(header, 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.item.id", is(bookingDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.booker.id", is(bookingDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus().toString())));
    }

    @Test
    void approveBooking() throws Exception {
        when(bookingService.approveBooking(anyLong(), anyLong(), anyBoolean())).thenReturn(bookingDto);
        mvc.perform(patch("/bookings/{id}", 1L).param("approved", "true").header(header, 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.item.id", is(bookingDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.booker.id", is(bookingDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus().toString())));
    }

    @Test
    void getBooking() throws Exception {
        when(bookingService.getBooking(anyLong(), anyLong())).thenReturn(bookingDto);
        mvc.perform(get("/bookings/{id}", 1L).header(header, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.item.id", is(bookingDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.booker.id", is(bookingDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus().toString())));
    }

    @Test
    void getOwnerBooking() throws Exception {
        when(bookingService.getBookingsByOwner(anyLong(), any())).thenReturn(List.of(bookingDto));
        mvc.perform(get("/bookings/owner", 1L).header(header, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(1)))
                .andExpect(jsonPath("$[0].id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].item.id", is(bookingDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$[0].booker.id", is(bookingDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$[0].status", is(bookingDto.getStatus().toString())));
    }

    @Test
    void getBookings() throws Exception {
        when(bookingService.getBookings(anyLong(), any())).thenReturn(List.of(bookingDto));
        mvc.perform(get("/bookings", 1L).header(header, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(1)))
                .andExpect(jsonPath("$[0].id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].item.id", is(bookingDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$[0].booker.id", is(bookingDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$[0].status", is(bookingDto.getStatus().toString())));
    }

    @Test
    void getBookingNotFoundException() throws Exception {
        when(bookingService.getBooking(anyLong(), anyLong()))
                .thenThrow(new NotFoundException("Booking not found"));
        mvc.perform(get("/bookings/{id}", 1L).header(header, 1))
                .andExpect(status().isNotFound());
    }

    @Test
    void getBookingForbiddenException() throws Exception {
        when(bookingService.getBooking(anyLong(), anyLong()))
                .thenThrow(new ForbiddenException("Not authorized"));
        mvc.perform(get("/bookings/{id}", 1L).header(header, 1))
                .andExpect(status().isForbidden());
    }

    @Test
    void createBookingValidateException() throws Exception {
        when(bookingService.createBooking(anyLong(), any()))
                .thenThrow(new BadRequestException("Wrong dates"));
        mvc.perform(post("/bookings").content(mapper.writeValueAsString(bookingDto)).header(header, 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

}
