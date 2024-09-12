package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.booking.enm.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@DataJpaTest
class BookingRepositoryTest {
    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private TestEntityManager em;

    @Test
    void findByBookerIdAndStatus() {
        User user = new User(null, "mail@du.tu", "name");
        Item item = new Item(null, "item", "item descr", true, user, null);
        Booking booking = new Booking(null, LocalDateTime.now(), LocalDateTime.now().plusMinutes(10), item, user, BookingStatus.WAITING);
        em.persist(user);
        em.persist(item);
        em.persist(booking);
        em.flush();

        List<Booking> bookings = bookingRepository.findByBooker_IdAndStatusOrderByStartDesc(user.getId(), BookingStatus.WAITING);
        assertThat(bookings.size(), equalTo(1));
        assertThat(bookings.getFirst(), equalTo(booking));
    }
}
