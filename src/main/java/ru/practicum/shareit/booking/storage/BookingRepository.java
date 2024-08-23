package ru.practicum.shareit.booking.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.enm.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByBooker_IdAndStatusOrderByStartDesc(Long userId, BookingStatus bookingStatus);

    List<Booking> findByBooker_IdAndStartIsAfterOrderByStartDesc(Long userId, LocalDateTime now);

    List<Booking> findByBooker_IdAndEndIsBeforeOrderByStartDesc(Long userId, LocalDateTime now);

    List<Booking> findByBooker_IdOrderByStartDesc(Long userId);

    List<Booking> findBookingsByItemOwnerOrderByStartDesc(User user);

    List<Booking> findByItem_Id(Long itemId);

    List<Booking> findBookingsByItemOwnerAndStatusOrderByStartDesc(User user, BookingStatus bookingStatus);

    List<Booking> findBookingsByItemOwnerAndEndIsBeforeOrderByStartDesc(User user, LocalDateTime now);

    List<Booking> findBookingsByItemOwnerAndStartIsAfterOrderByStartDesc(User user, LocalDateTime now);

    List<Booking> findByBooker_IdAndItem_IdAndStatusAndEndIsBefore(Long userId, Long itemId, BookingStatus bookingStatus, LocalDateTime date);
}
