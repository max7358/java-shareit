package ru.practicum.shareit.booking;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private final BookingClient bookingClient;

    @GetMapping
    public ResponseEntity<Object> getBookings(@RequestHeader("X-Sharer-User-Id") Long userId,
                                              @RequestParam(name = "state", defaultValue = "all") String stateParam) {
        BookingState state = BookingState.from(stateParam).orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
        log.info("Getting bookings with state: {}", state);
        return bookingClient.getBookings(userId, state);
    }

    @PostMapping
    public ResponseEntity<Object> createBooking(@RequestHeader("X-Sharer-User-Id") Long userId, @Valid @RequestBody BookItemRequestDto bookingDto) {
        log.info("Creating booking {}, userId={}", bookingDto, userId);
        return bookingClient.createBooking(userId, bookingDto);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long bookingId) {
        log.info("Get booking {}, userId={}", bookingId, userId);
        return bookingClient.getBooking(userId, bookingId);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approveBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                 @PathVariable Long bookingId, @RequestParam boolean approved) {
        log.info("Approving booking: {}, user: {}, approved: {}", bookingId, userId, approved);
        return bookingClient.approveBooking(userId, bookingId, approved);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getBookingsByOwner(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                     @RequestParam(name = "state", defaultValue = "all") String stateParam) {
        BookingState state = BookingState.from(stateParam).orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
        log.info("Getting bookings by owner {} with state: {}", state, userId);
        return bookingClient.getBookingsByOwner(userId, state);
    }
}
